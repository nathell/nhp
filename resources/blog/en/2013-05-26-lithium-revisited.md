---
date: 2013-05-26
title: "Lithium revisited: A 16-bit kernel (well, sort of) written in Clojure (well, sort of)"
categories: Clojure programming
---

Remember [Lithium][0]? The x86 assembler written in Clojure, and a simple stripes effect written in it? Well, here’s another take on that effect:

<img src="/img/blog/stripes2.png">

And here is the source code:

```clojure
(do (init-graph)
    (loop [x 0 y 0]
      (put-pixel x y (let [z (mod (+ (- 319 x) y) 32)]
                       (if (< z 16) (+ 16 z) (+ 16 (- 31 z)))))
      (if (= y 200)
        nil
        (if (= x 319)
          (recur 0 (inc y))
          (recur (inc x) y)))))
```

I’ve implemented this several months ago, pushed it to Github and development has pretty much stalled since then. And after seeing [this recent post][1] on HN today, I’ve decided to give Lithium a little more publicity, in the hope that it will provide a boost of motivation to me. Because what we have here is pretty similar to Rustboot: it’s a 16-bit kernel written in Clojure.

Well, sort of.

After writing a basic assembler capable of building bare binaries of simple x86 real-mode programs, I’ve decided to make it a building block of a larger entity. So I’ve embarked on a project to implement a compiler for a toy Lisp-like language following the paper [“An Incremental Approach to Compiler Construction”][2], doing it in Clojure and making the implemented language similar to Clojure rather than to Scheme.

(Whether it actually can be called Clojure is debatable. It’s unclear what the definition of Clojure the language is. Is running on JVM a part of what makes Clojure Clojure? Or running on any host platform? Is ClojureScript Clojure? What about ClojureCLR, or clojure-py?)

So far I’ve only gotten to step 7 of 24 or so, but that’s already enough to have a working `loop/recur` implementation, and it was trivial to throw in some graphical mode 13h primitives to be able to implement this effect.

By default I’m running Lithium programs as DOS .COM binaries under DOSBox, but technically, the code doesn’t depend on DOS in any way (it doesn’t ever invoke interrupt 21h) and so it can be combined with a simple bootloader into a kernel runnable on the bare metal.

The obligatory HOWTO on reproducing the effect: install DOSBox and Leiningen, checkout [the code][3], launch a REPL with `lein repl`, execute the following forms, and enjoy the slowness with which individual pixels are painted:

```clojure
(require 'lithium.compiler)
(in-ns 'lithium.compiler)
(run! (compile-program "/path/to/lithium/examples/stripes-grey.clj"))
```

 [0]: http://blog.danieljanus.pl/blog/2012/05/14/lithium/
 [1]: https://news.ycombinator.com/item?id=5771276
 [2]: http://scheme2006.cs.uchicago.edu/11-ghuloum.pdf
