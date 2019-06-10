---
date: 2019-02-05
title: Re-framing text-mode apps
categories: Clojure programming
---

## Intro

> “But, you know, many explorers liked to go to places that are unusual. And, it’s only for the fun of it.”
> – Richard P. Feynman

A couple of nights ago, I hacked together a small Clojure program.

All it does is displays a terminal window with a red rectangle in it. You can use your cursor keys to move it around the window, and space bar to change its colour. It’s fun, but it doesn’t sound very useful, does it?

In this post, I’ll try to convince you that there’s more to this little toy than might at first sight appear. You may want to check out [the repo](https://github.com/nathell/clj-tvision) as you go along.

## In which an unexpected appearance is made

(I’ve always envied [Phil Hagelberg](https://technomancy.us) this kind of headlines.)

As you might have guessed from this article’s title, clj-tvision (a working name for the program) is a [re-frame](https://github.com/Day8/re-frame) app.

For those of you who haven’t heard of re-frame, a word of explanation: it’s a ClojureScripty way of writing React apps, with Redux-like management of application state. If you do know re-frame (shameless plug: we at [WorksHub](https://works-hub.com) do, and use it a lot: it powers the site you’re looking at right now!), you’ll instantly find yourself at home. However, a few moments later, a thought might dawn upon you, and you might start to feel a little uneasy…

Because I’ve mentioned React and ClojureScript, and yet I’d said earlier that we’re talking a text-mode application here. And I’ve mentioned that it’s written in Clojure. It is, in fact, not using React at all, and it has nothing to do whatsoever with ClojureScript, JavaScript, or the browser.

How is that even possible?

Here’s the catch: re-frame is implemented in `.cljc` files. So while it’s mostly used in the ClojureScript frontend, it _can_ be used from Clojure. You may know this if you’re testing your events or subscriptions on the JVM.

While it’s mostly – if not hitherto exclusively – used for just that, I wanted to explore whether it could be used to manage state in an actual, non-web app. Text-mode is a great playground for this kind of exploration. Rather than picking a GUI toolkit and concern myself with its intricacies, I chose to just put things on a rectangular sheet of text characters.

(But if you are interested in pursuing a React-ish approach for GUIs, check out what Bodil Stokke’s been doing in [vgtk](https://github.com/bodil/vgtk).)

## Living without the DOM

The building blocks of a re-frame app are subscriptions, events, and views. While the first two work in Clojureland pretty much the same way they do in the browser (although there are differences, of which more anon), views are a different beast.

[re-frame’s documentation](https://github.com/Day8/re-frame/blob/master/docs/SubscriptionFlow.md) says that views are “data in, Hiccup out. Hiccup is ClojureScript data structures which represent DOM.” But outside of the browser realm, there’s no DOM. So let’s rephrase that more generally: re-frame views should produce _data structures which declaratively describe the component’s appearance to the user_. In web apps, those structures correspond to the DOM. What they will look like outside is up to us. We’ll be growing our own DOM-like model, piecemeal, as needs arise.

For clj-tvision, I’ve opted for a very simple thing. Let’s start with a concrete example. Here’s a view:

```clojure
(defn view []
  [{:type :rectangle, :x1 10, :y1 5, :x2 20, :y2 10, :color :red}])
```

Unlike in the DOM, in this model the UI state isn’t a tree. It’s a flat sequence of maps that each represent individual “primitive elements”. We could come up with a fancy buzzword-compliant name and call it Component List Model, or CLiM for short, in homage to [the venerable GUI toolkit](https://en.wikipedia.org/wiki/Common_Lisp_Interface_Manager).

Like normal re-frame views, CLiM views can include subviews. An example follows:

```clojure
(defn square [left top size color]
  [{:type :rectangle,
    :x1 left,
    :y1 top,
    :x2 (+ left size -1),
    :y2 (+ top size -1),
    :color color}])

(defn view []
  [[square 1 1 5 :red]
   [square 9 9 5 :blue]])
```

How to render a view? Simple. First, flatten the list, performing funcalls on subviews so that you get a sequence containing only primitives. Then, draw each of them in order. (If there is an overlap, the trailers will obscure the leaders. Almost biblical.)

I’ve defined a multimethod, `render-primitive`, dispatching on `:type`. Its methods draw the corresponding primitive to a Lanterna screen.

Oh, didn’t I mention [Lanterna](https://github.com/mabe02/lanterna)? It’s a Java library for terminals. Either real ones or emulated in Swing (easier to work with when you’re in a CIDER REPL). Plus, it sports virtual screens which can be blitted to a real terminal. This gives us a rough poor man’s equivalent of React’s VDOM. And it has a [Clojure wrapper](https://github.com/AvramRobert/clojure-lanterna)!

## Events at eventide

So now we know how to draw our UI. But an app isn’t made up of just drawing. It has a main loop: it listens to events, which cause the app state to change and the corresponding components to redraw.

re-frame does provide an event mechanism, but it doesn’t _define_ any events per se. So we need to ask ourselves: who calls _dispatch_? How do events originate? How to write the main loop?

clj-tvision is a proof-of-concept, so it doesn’t concern itself with mouse support. There’s only one way a user can interact with the app: via the keyboard. So keystrokes will be the only “source events”, as it were, for the app; and so writing the event loop should be simple. Sketching pseudocode:

```clojure
(loop []
  (render-app)
  (let [keystroke (wait-for-key)] ;; blocking!
    (dispatch [:key-pressed keystroke])
    (recur)))
```

Simple as that, should work, right?

Wrong.

If you actually try that, it’ll _somewhat_ work. Hit right arrow to move the rectangle, nothing happens! Hit right arrow again, it moves. Hit left, it moves right. Hit right, it moves left. Not what you want.

You see, there’s a complication stemming from the fact that re-frame’s events are asynchronous by default. (Hence the `dispatch` vs. `dispatch-sync` dichotomy.) They don’t get dispatched immediately; rather, re-frame places them on a queue and processes them asynchronously, so that they don’t hog the browser. The Clojure version of re-frame handles that using a single-threaded executor with a dedicated thread.

We _almost_ could use `dispatch-sync` everywhere, but for re-frame that’s a no-no: once within a `dispatch-sync` handler, you cannot dispatch other events. If you try anyway, re-frame will detect it and politely point its dragon-scaly head at you, explaining it doesn’t like it. (It is a benevolent dragon, you know.)

So we need to hook into that “next-tick” machinery of re-frame’s somehow. There are probably better ways of doing this, but I opted to blatantly redefine `re-frame.interop/next-tick` to tell the main loop: “hey, events have been handled and we have a new state, dispatch an event so we can redraw.” This is one of the rare cases where monkey-patching third-party code with `alter-var-root` saves you the hassle of forking that entire codebase.

So now we have _two_ sources of events: keystrokes, and `next-tick`. To multiplex them, I’ve whipped up a channel with core.async. Feels hacky, but allows to add mouse support in the future. Or time-based events that will be fired periodically every so often.

For completeness, I should also add that Clojure-side re-frame doesn’t have the luxury of having reactive atoms provided by Reagent. Its ratoms are ordinary Clojure atoms. Unlike in ClojureScript, any time the app state changes, _every_ subscription in the signal graph will be recomputed. It may well be possible to port Reagent’s ratoms to Clojure, but it is a far more advanced exercise. For simple apps, what re-frame provides on its own might just be enough.

And with that final bit, we can swipe all that hackitude under the carpet… or, should I say, tuck it into an internal ns that hopefully no-one will ever look into. And we’re left with shiny, declarative, re-framey, beautiful UI code on the surface. [Just look](https://github.com/nathell/clj-tvision/blob/master/src/tvision/core.clj).

## Closing thoughts

> “Within C++, there is a much smaller and cleaner language struggling to get out.”
> – Bjarne Stroustrup

If you’ve ever encountered legacy C++ code, this will ring true. Come to think of it, Stroustrup’s words are true of every system that has grown organically over its lifetime, with features being added to it but hardly ever removed.

And modern webapps may well be the epitome of that kind of system. We now have desktop apps that are fully self-contained on a single machine, yet use an overwhelmingly complex and vast machinery grown out of a simple system originally devised to [view static documents over the Internet](http://info.cern.ch/hypertext/WWW/TheProject.html).

For all that complexity, we continue to use it. Partly owing to its ubiquity, partly for convenience. In my experience, the abstractions provided by re-frame allow you to wrap your head around large apps and reason about them much more easily than, say, object-oriented approaches. It just feels right. Conversely, writing an app in, say, GTK+ would now feel like a setback by some twenty years.

So this toy, this movable rectangle on a black screen, is not so much an app as it is a philosophical exercise. It is what my typing fingers produced while I pondered, weak and weary: “can we throw away most of that cruft, while still enjoying the abstractions that make life so much easier?”

Can we?

This post was originally published on [Functional Works](https://functional.works-hub.com/learn/re-framing-text-mode-apps-fd5cf).
