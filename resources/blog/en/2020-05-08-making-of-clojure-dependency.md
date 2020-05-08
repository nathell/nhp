---
date: 2020-05-08
title: Making of “Clojure as a dependency”
categories: Lisp Clojure programming
---

In my previous post, [“Clojure as a dependency”][1], I’ve presented the results of some toy research on Clojure version numbers seen in the wild. I’m a big believer in [reproducible research][2], so I’m making available a [Git repo][3] that contains code you can run yourself to reproduce these results. This post is an experience report from writing that code.

There are two main components to this project: acquisition and analysis of data (implemented in the namespaces `versions.scrape` and `versions.analyze`, respectively). Let’s look at each of these in turn.

## Data acquisition

This step uses the [GitHub API v3][4] to:

- retrieve the 1000 most popular Clojure repositories (using the [Search repositories][5] endpoint and going through all [pages][6] of the paginated result);
- for each of these repositories, look at its file list (in the master branch) and pick up any files named `project.clj` or `deps.edn` in the root directory, using the [Contents][7] endpoint);
- parse each of these files and extract the list of dependencies.

As hinted by the namespace, I’ve opted to use [Skyscraper][8] to orchestrate the process. It would arguably have been simpler to use GitHub’s [GraphQL v4 API][12], but I wanted to showcase Skyscraper’s custom parsing facilities.

There’s no actual HTML scraping going on (all processors use either JSON or Clojure parsers), but Skyscraper is still able to “restructure” the result – traverse the graph endpoint in a manner similar to that of GraphQL – with very little effort. It would have been possible with any other RESTful API. Plus, we get goodies like caching or tree pruning for free.

Most of the code is straightforward, but parsing of `project.clj` merits some explanation. Some of my initial assumptions proved incorrect, and it’s fun to see how. I initially tried to use [`clojure.edn`][13], but Leiningen project definitions are not actually EDN – they are Clojure code, which is a superset of EDN. So I had to resort to `read-string` from core – with `*read-eval*` bound to nil (otherwise the code would have a Clojure injection vulnerability – think [Bobby Tables][14]). Needless to say, some `project.clj`s turned out to depend on read-eval.

Some projects (I’m looking at you, [Closh][9], [Babashka][10] and [sci][11]) keep the version number outside of `project.clj`, in a text file (typically in `resources/`), and slurp it back into `project.clj` with a read-eval’d expression:
```clojure
(defproject closh-sci
  #=(clojure.string/trim
     #=(slurp "resources/CLOSH_VERSION"))
  …)
```

A trick employed by one project, [Metabase][15], is to dynamically generate JVM options containing a port number at parse time, so that test suites running at the same time don’t clash with each other:

```clojure
#=(eval (format "-Dmb.jetty.port=%d" (+ 3001 (rand-int 500))))
```

Finally, it turned out that `defproject` is not always a first form in `project.clj`. Some projects, like [bridge][17], only contain a placeholder `project.clj` with no forms; others, like [aleph][16], first define some constants, and then refer to them in a `defproject` form. If those constants contain parts of the dependencies list, then those dependencies won’t be processed correctly. Fortunately, not a lot of projects do this, so it doesn’t skew the results much.

Anyway, the end result of the acquisition phase is a sequence of maps describing project definitions. They look like this:

```clojure
{:name "clojure-koans",
 :full-name "functional-koans/clojure-koans",
 :deps-type :leiningen,
 :page 1,
 :deps {org.clojure/clojure #:mvn{:version "1.10.0"},
        koan-engine #:mvn{:version "0.2.5"}}},
 :profile-deps {:dev {lein-koan #:mvn{:version "0.1.5"}}}
```

Homogeneity is important: every dependency description has been converted to the cli-tools format, even if it comes from a `project.clj`.

## Data analysis

I’ve long been searching for a way to do exploratory programming in Clojure without turning the code to a tangled mess, portable only along with my computer.

Exploratory (or research) programming is very different from “normal” programming. In the latter, most of the time you typically focus on a coherent project – a program or a library. In contrast, in the former, you spend a lot of time in the REPL, trying all sorts of different things and `def`ing new values derived from already computed ones.

This is very convenient, but it’s extremely easy to get carried away in the REPL and get lost in a sea of `def`s. If you want to redo your computations from scratch, just about your only option is to take your REPL transcript and re-evaluate the expressions one by one, in the correct order. Cleaning up the code (e.g. deglobalizing) as you go is very difficult.

I’ve found an answer: [Plumatic Graph][18], part of the [plumbing][19] library. There are a plethora of uses for it: for example, at [Fy][20], my current workplace, we’re using it to define our test fixtures.
But as it turns out, it makes exploratory programming enjoyable.

The bulk of code in [`versions.analyze`][23] consists of a big definition of a graph, with nodes representing computations – things that I’d normally have `def`’d in a REPL. Consequently, most of these
definitions are short and to the point. I also gave the nodes verbose, descriptive, explicit names. Name and conquer. `raw-repos` is the output from data acquisition, `repos` is an all-important node
containing those `raw-repos` that were successfully parsed, and most other things depend on it.

It also doesn’t obstruct much the normal REPL research flow. My normal workflow with REPL and Graph is something along the lines of:

1. `(def result (main))`
2. evaluate something using inputs from `result`
3. nah, it leads nowhere
4. evaluate something else
5. hey, that’s interesting!
6. add a new node to the graph definition
7. GOTO 1

Thanks to Graph’s lazy compiler, I can re-evaluate anything at need and have it evaluate only the things needed, and nothing else. Also, because the graph is explicit, it’s fairly easy to [visualize it][24]. (Click the image to open it in full-size in another tab.)

<a href="/img/blog/computation-graph.png" target="_blank"><img src="/img/blog/computation-graph.png"></a>

Because it’s lazy, it doesn’t hurt to put extra things in there just in case, even when you’re not going to report them. For example, I was curious what things besides a version number people put in dependencies. `:exclusions`, for sure, but what else? This is the `:what-other-things-besides-versions` node.

Imagine my surprise when I found `:exlusions` (_sic_) in there, which turned out to be a typo in shadow-cljs’ `project.clj`! I submitted [a PR][21], and Thomas Heller merged it a few days after.

My only gripe with Graph is that it runs somewhat contrary to the current trends in the Clojure community: for example, it doesn’t support namespaced keywords (although there’s an [open ticket][22] for that).
But on the whole, I’m sold. I’ll definitely be using it in the next piece of research in Clojure, and I’m on a lookout for something similar in pure R. If you know something, do tell me!

## Some words on plotting

The plot from previous post has been generated in pure R, using [ggplot2][25] (an extremely versatile API). Clojure generates a CSV with munged data, and then R reads that CSV as a data frame and generates the plot in a few lines.

I’ve briefly played around with [clojisr][26], a bridge between Clojure and R. It was an enlightening experiment, and it would let me avoid the intermediate CSV, but I decided to ditch it for a few reasons:

- It pulls in quite a few dependencies (I wanted to keep them down to a minimum), and requires some previous setup on the R side.
- I’d much rather write my R as R, since I’m comfortable with it, rather than spend time wondering how it maps to Clojure. This is similar to the SQL story: these days I prefer [HugSQL][27] over [Korma][28], unless I have good reasons to choose otherwise.
- clojisr opens up a child R process just by `require`ing a namespace. I’m not a fan of that.

But it’s definitely very promising! I applaud the effort and I’ll keep a close eye on it.

## Key takeaways

- Skyscraper makes data acquisition bearable, if not fun.
- Plumatic Graph makes writing research code in Clojure fun.
- ggplot makes plotting data fun.
- Clojure makes programming fun. (But you knew that already.)

 [1]: /2020/05/02/clojure-dependency/
 [2]: https://en.wikipedia.org/wiki/Reproducibility#Reproducible_research
 [3]: https://github.com/nathell/versions
 [4]: https://developer.github.com/v3/
 [5]: https://developer.github.com/v3/search/#search-repositories
 [6]: https://developer.github.com/v3/#pagination
 [7]: https://developer.github.com/v3/repos/contents/
 [8]: https://github.com/nathell/skyscraper
 [9]: https://github.com/dundalek/closh
 [10]: https://github.com/borkdude/babashka
 [11]: https://github.com/borkdude/sci
 [12]: https://developer.github.com/v4/
 [13]: https://clojure.github.io/clojure/clojure.edn-api.html#clojure.edn/read
 [14]: https://xkcd.com/327/
 [15]: https://github.com/metabase/metabase
 [16]: https://github.com/ztellman/aleph
 [17]: https://github.com/robert-stuttaford/bridge
 [18]: https://plumatic.github.io/prismatics-graph-at-strange-loop
 [19]: https://github.com/plumatic/plumbing
 [20]: https://iamfy.co
 [21]: https://github.com/thheller/shadow-cljs/pull/699
 [22]: https://github.com/plumatic/plumbing/issues/126
 [23]: https://github.com/nathell/versions/blob/master/src/clj/versions/analyze.clj#L41
 [24]: https://github.com/RedBrainLabs/graph-fnk-viz
 [25]: https://ggplot2.tidyverse.org
 [26]: https://github.com/scicloj/clojisr
 [27]: https://www.hugsql.org
 [28]: https://github.com/korma/Korma
