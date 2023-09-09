---
date: 2023-09-09
title: My mental model of transducers
categories: programming clojure
---

## Intro

I’ve been programming in Clojure for a long time, but I haven’t been using transducers much. I learned to mechanically transform `(into [] (map f coll))` to `(into [] (map f) coll)` for a slight performance gain, but not much beyond that. Recently, however, I’ve found myself refactoring transducers-based code at work, which prompted me to get back to speed.

I found Eero Helenius’ article [“Grokking Clojure transducers”][1] a great help in that. To me, it’s much more approachable than the [official documentation][2] – in a large part because it shows you how to build transducers from the ground up, and this method of learning profoundly resonates with me. I highly recommend it. However, it’s also useful to have a visual intuition of how transducers work, a mental model that hints at the big picture without zooming into the details too much. In this post, I’d like to share mine and illustrate it with a REPL session. (Spoiler alert: there’s [core.async][3] ahead, but in low quantities.)

## Pictures

Imagine data flowing through a conveyor belt. Say, infinitely repeating integers from 1 to 5:

<img src="/img/blog/conveyor-belt.svg" alt="Conveyor belt">

I’m using the abstract term “conveyor belt”, rather than “sequence” or something like this, to avoid associations with any implementation details. Just pieces of data, one after another. These data may be anything; they may flow infinitely or stop at some point; may or may not all exist in memory at the same time. Doesn’t matter. That’s the beauty of transducers: they completely abstract away the implementation of sequentiality.

So, what is a transducer, intuitively? It’s a mechanism for _transforming conveyor belts into other conveyor belts_.

For example, `(map inc)` is a transducer that says: “take this conveyor belt and produce one where every number is incremented”. Applying it to the above belt yields this one:

<img src="/img/blog/conveyor-belt-2.svg" alt="Conveyor belt, transformed">

An important thing about transducers is that they’re _composable_. To understand that, imagine further transforming the above belt by removing all the odd numbers. Intuitively, that’s what `(remove odd?)` does:

<img src="/img/blog/conveyor-belt-3.svg" alt="Conveyor belt, transformed again">

(I’ve left the spacing between boxes the same as before, because it helps me visualise `(remove odd?)` better. I imagine an invisible gnome sitting above the belt, watching carefully all the boxes that pass below it, and snatching greedily every one that happens to contain an odd number.)

Composability means that Clojure lets you say `(comp (map inc) (remove odd?))` to mean the transducer that transforms the first belt to the third one. By putting together two simple building blocks, we produced a more complex one – that it itself reusable and can be used as another building block in an ever more complex data pipeline.

Notice we _still_ haven’t said anything about the actual representation of the data, but are already able to model complex processes. We can then apply them to actual data, whether it’s a simple vector-to-vector transformation within the same JVM, or listening to a topic on a Kafka cluster, summarizing the incoming data and sending them to a data warehouse.

## Code

OK, enough handwaving, time for a demo. Let’s fire up a REPL and load core.async (I’m assuming you’ve added it to your dependencies already). I won’t reproduce here the resulting values of expressions we evaluate (they’re mostly `nil`s anyway), but I will reproduce output from the REPL (as comments).

```clojure
(require '[clojure.core.async :refer [chan <!! >!! thread close!]])
```

Why core.async? Because I find it a great way to implement a conveyor belt that you can play with interactively. This can help you understand how the various Clojure-provided transducers work. For the noncognoscenti: core.async is a Clojure library that allows you to implement concurrent processes that communicate over _channels_. By default, that communication is synchronous, meaning that if a process tries to read from a channel, it blocks until another process writes something to that channel.

As it happens, we can pass a transducer to the function that creates channels, `chan`. It will put the invisible gnomes to work on values that pass through the channel. So you can view that channel as a conveyor belt!

For easy tinkering, we can do this:

```clojure
(defn transformed-belt [xf]
  (let [ch (chan 1 xf)]
    (thread
      (loop []
        (when-some [value (<!! ch)]
          (println "Value:" (pr-str value)))
          (recur)))
    ch))
```

This fires up a process working at the receiving end of the conveyor belt. It will print out any transformed values as soon as they become available. Typing at the REPL, we will assume the role of producer, putting data on the belt.

Like this:

```clojure
(def b (transformed-belt (map inc)))
(>!! b 2)
; Value: 3
(>!! b 42)
; Value: 43
```

It works! We’re putting in numbers, and out come the incremented ones.

When we’re done experimenting with the belt, we need to `close!` it. This will cause the worker thread to shutdown.

```clojure
(close! b)
```

We can now experiment with something more complex, like that combined transducer we’ve talked about before:

```clojure
(def b (transformed-belt (comp (map inc) (remove odd?))))
(>!! b 1)
; Value: 2
(>!! b 2)
(>!! b 3)
; Value: 4
```

We got the transformed 1 and 3, but the intermediate value for 2 was odd, so it was snatched by the gnome and we never saw it.

There’s even more fun to be had! Let’s try `(partition-all 3)`:

```clojure
(close! b)
(def b (transformed-belt (partition-all 3)))
(>!! b 1)
```

Nothing…

```clojure
(>!! b 2)
```

Still nothing…

```clojure
(>!! b 3)
; Value: [1 2 3]
```

Blammo! Our gnome is now packaging together incoming items into bundles of three, caching them in the interim while the bundle is not complete yet. But if we close the input prematurely, it will acknowledge and produce the incomplete bundle:

```clojure
(>!! b 4)
(>!! b 5)
(close! b)
; Value: [4 5]
```

In fact, `partition-all` is what prompted me to write this post. That code at work I mentioned actually included a transducer composition that had a `(net.cgrand.xforms/into [])` in it. That transducer (from Christophe Grand’s [xforms][4] library) accumulates data until there’s nothing more to accumulate, and then emits all of it as one large vector. By replacing it with `partition-all`, I altered the downstream processing to handle multiple smaller batches rather than one huge batch, improving the system’s latency.

A small change for a huge win. Clojure continues to amaze me.

Plus, it’s fun to make JS-less animations in SVG. :)

 [1]: https://dev.solita.fi/2021/10/14/grokking-clojure-transducers.html
 [2]: https://clojure.org/reference/transducers
 [3]: https://github.com/clojure/core.async
 [4]: https://github.com/cgrand/xforms/
