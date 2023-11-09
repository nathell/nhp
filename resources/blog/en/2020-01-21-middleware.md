---
date: 2020-01-21
title: Careful with that middleware, Eugene
categories: Clojure programming Skyscraper
---

## Prologue

I’ll be releasing version 0.3 of [Skyscraper][1], my Clojure framework for scraping entire sites, in a few days.

More than three years have passed since its last release. During that time, I’ve made a number of attempts at redesigning it to be more robust, more usable, and faster; the last one, resulting in an almost complete rewrite, is now almost ready for public use as I’m ironing out the rough edges, documenting it, and adding tests.

It’s been a long journey and I’ll blog about it someday; but today, I’d like to tell another story: one of a nasty bug I had encountered.

## Part One: Wrap, wrap, wrap, wrap

While updating the code of one of my old scrapers to use the API of Skyscraper 0.3, I noticed an odd thing: some of the output records contained scrambled text. Apparently, the character encoding was not recognised properly.

“Weird,” I thought. Skyscraper should be extra careful about honoring the encoding of pages being scraped (declared either in the headers, or the `<meta http-equiv>` tag). In fact, I remembered having seen it working. What was wrong?

For every page that it downloads, Skyscraper 0.3 caches the HTTP response body along with the headers so that it doesn’t have to be downloaded again; the headers are needed to ensure proper encoding when parsing a cached page. The headers are lower-cased, so that Skyscraper can then call `(get all-headers "content-type")` to get the encoding declared in headers. If this step is missed, and the server returns the encoding in a header named `Content-Type`, it won’t be matched. Kaboom!

I looked at the cache, and sure enough, the header names in the cache were not lower-cased, even though they should be. But why?

Maybe I was mistaken, and I had forgotten the lower-casing after all? A glance at the code: no. The lower-casing was there, right around the call to the download function.

Digression: Skyscraper uses [clj-http][2] to download pages. clj-http, in turn, uses the [middleware pattern][3]: there’s a “bare” request function, and then there are wrapper functions that implement things like redirects, OAuth, exception handling, and what have you. I say “wrapper” because they literally wrap the bare function: `(wrap-something request)` returns another function that acts just like `request`, but with added functionality. And that other function can in turn be wrapped with yet another one, and so on.

There’s a default set of middleware wrappers defined by clj-http, and it also provides a macro,
`with-additional-middleware`, which allows you to specify additional wrappers. One such wrapper is
`wrap-lower-case-headers`, which, as the name suggests, causes the response’s header keys to
be returned in lower case.

Back to Skyscraper. We’re ready to look at the code now. Can you spot the problem?

```clojure
(let [request-fn (or (:request-fn options)
                     http/request)]
  (http/with-additional-middleware [http/wrap-lower-case-headers]
    (request-fn req
                success-fn
                error-fn)))
```

I stared at it for several minutes, did some dirty experiments in the REPL, perused the code of clj-http, until it dawned on me.

See that `request-fn`? Even though Skyscraper uses `http/request` by default, you can override it in the options to supply your own way of doing HTTP. (Some of the tests use it to mock calls to a HTTP server.) In this particular case, it was not overridden, though: the usual `http/request` was used. So things looked good: within the body of `http/with-additional-middleware`, headers should be lower-cased because `request-fn` is `http/request`.

Or is it?

Let me show you how `with-additional-middleware` is implemented. It expands to another macro, `with-middleware`, which is defined as follows (docstring redacted):

```clojure
(defmacro with-middleware
  [middleware & body]
  `(let [m# ~middleware]
     (binding [*current-middleware* m#
               clj-http.client/request (reduce #(%2 %1)
                                               clj-http.core/request
                                               m#)]
       ~@body)))
```

That’s right: `with-middleware` works by dynamically rebinding `http/request`. Which means the `request-fn` I was calling is not actually the wrapped version, but the one captured by the outer `let`, the one that wasn’t rebound, the one without the additional middleware!

After this light-bulb moment, I moved `with-additional-middleware` outside of the `let`:

```clojure
(http/with-additional-middleware [http/wrap-lower-case-headers]
  (let [request-fn (or (:request-fn options)
                       http/request)]
    (request-fn req
                success-fn
                error-fn)))
```

And, sure enough, it worked.

## Part Two: The tests are screaming loud

Is it the end of the story? I’m guessing you’re thinking it is. I thought so too. But I wanted to add one last thing: a regression test, so I’d never run into the same problem in the future.

I whipped up a test in which one ISO-8859-2-encoded page was scraped, and a check for the correct string was made. I ran it against the fixed code. It was green. I ran it against the previous, broken version…

It was _green_, too.

At this point, I knew I had to get to the bottom of this.

Back to experimenting. After a while, I found out that extracting encoding from a freshly-downloaded page actually worked fine! It only failed when parsing headers fetched from a cache. But the map was the same in both cases! In both cases, the code was effectively doing

```clojure
(get {"Content-Type" "text/html; charset=ISO-8859-2"}
     "content-type")
```

This lookup _shouldn’t_ succeed: in map lookup, string comparison is case-sensitive. And yet, for freshly-downloaded headers, it _did_ succeed!

I checked the `type` of both maps. One of them was a `clojure.lang.PersistentHashMap`, as expected. The other one was not. It was actually a `clj_http.headers.HeaderMap`.

I’ll let the comment of that one speak for itself:

> a map implementation that stores both the original (or canonical)
> key and value for each key/value pair, but performs lookups and
> other operations using the normalized – this allows a value to be
> looked up by many similar keys, and not just the exact precise key
> it was originally stored with.

And so it turned out that the library authors have actually foreseen the need for looking up headers irrespective of case, and provided a helpful means for that. The whole lowercasing business was not needed, after all!

I stripped out the `with-additional-middleware` altogether, added some code elsewhere to ensure that the header map is a `HeaderMap` regardless of whether it comes from the cache or not, and they lived happily ever after.

## Epilogue

Moral of the story? It’s twofold.

 - Dynamic rebinding can be dangerous. Having a public API that is implemented in terms of dynamic rebinding, even more so. I’d prefer if clj-http just allowed the custom middleware to be explicitly specified as an argument, thusly:
```clojure
(http/request req
              :additional-middleware [http/wrap-lower-case-headers])
```


 - Know your dependencies. If you have a problem that might be generically addressed by the library you’re using, look deeper. It might be there already.

Thanks to [3Jane][4] for proofreading this article.

 [1]: https://github.com/nathell/skyscraper
 [2]: https://github.com/dakrone/clj-http
 [3]: http://clojure-doc.org/articles/cookbooks/middleware.html
 [4]: https://www.3jane.co.uk
