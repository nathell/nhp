---
title: A case for symbol capture
date: 2010-04-05
categories: Clojure programming
---

Clojure by default protects macro authors from incidentally capturing a local symbol. Stuart Halloway [describes this][1] in more detail, explaining why this is a Good Thing. However, sometimes this kind of symbol capture is called for. I’ve encountered one such case today while hacking a Swing application.

As I develop the app, I find new ways to express Swing concepts and interact with Swing objects in a more Clojuresque way, so a library of GUI macros and functions gets written. One of them is a `wizard` macro for easy creation of installer-like wizards, where there is a sequence of screens that can be navigated with _Back_ and _Next_ buttons at the bottom of the window.

The API (certainly not finished yet) currently looks like this:

```clojure
(wizard & components)
```

where each Swing `component` corresponding to one wizard screen can be augmented by a supplementary map, which can contain, _inter alia_, a function to execute upon showing the screen in question.

Now, I want those functions to be able to access the _Back_ and _Next_ buttons in case they want to disable or enable them at need. I thus want the API user to be able to use two symbols, `back-button` and `next-button`, in the macro body, and have them bound to the corresponding buttons.

It is crucial that these bindings be lexical and not dynamic. If they were dynamic, they would be only effective during the definition of the wizard, but not when my closures are invoked later on. Thus, my implementation looks like this:

```clojure
(defmacro wizard [& panels]
  `(let [~'back-button (button "< Back")
         ~'next-button (button "Next >")]
   (do-wizard ~'back-button ~'next-button ~(vec panels))))
```

where `do-wizard` is a private function implementing the actual wizard creation, and the `~'foo` syntax forces symbol capture.

By the way, if all goes well, this blog post should be the first one syndicated to Planet Clojure. Hello, Planet Clojure readers!

 [1]: http://blog.thinkrelevance.com/2008/12/17/on-lisp-clojure-chapter-9
