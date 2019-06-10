---
title: Clojure SET
date: 2010-02-10
categories: Clojure games programming
---

I’ve just taken a short breath off work to put [some code][1] on GitHub that I had written over one night some two months ago. It is an implementation of the [Set][2] game in Clojure, using Swing for GUI.

I do not have time to clean up or comment the code, so I’m leaving it as is for now; however, I hope that even in its current state it can be of interest, especially for Clojure learners.

Some random notes on the code:

- Clojure is concise! The whole thing is just under 250 lines of code, complete with game logic and the GUI. Of these, the logic is about 50 LOC. Despite this it reads clearly and has been a pleasure to write, thanks to Clojure’s supports for sets as a data structure (in vein of the game’s title and theme).
- There are no graphics included. All the drawing is done in the GUI part of code (I’ve replaced the canonical squiggle shape by a triangle and stripes by gradients, for the sake of easier drawing).
- I’ve toyed around with different Swing layout managers for this game. Back in the days when I wrote in plain Java, I used to use [TableLayout][3], but it has a non-free license; [JGoodies Forms][4] is also nice, but has a slightly more complicated API (and it’s an additional dependency, after all). In the end I’ve settled with the standard GridBagLayout, which is similar in spirit to those two, but requires more boilerplate to set up. As it turned out, simple macrology makes it quite pleasurable to use; see `add-gridbag` in the code for details.
- Other things of interest might be my function to randomly shuffle seqs, which strikes a nice balance between simplicity/conciseness of implementation and randomness; and a useful debugging macro.

Comments?

 [1]: http://github.com/nathell/setgame
 [2]: http://en.wikipedia.org/wiki/Set_(game)
 [3]: https://tablelayout.dev.java.net/
 [4]: http://www.jgoodies.com/freeware/forms/
