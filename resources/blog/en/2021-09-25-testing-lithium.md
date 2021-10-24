---
date: 2021-09-25
title: "Testing a compiler that can’t even print stuff out"
categories: Clojure programming
---

I’m enjoying a week-long vacation. In addition to other vacationy things (a trip to Prague, yay!), I wanted to do some off-work programming Just For Fun™ and revisit one of my dormant pet projects, to see if I can make some progress.

I opted for Lithium, my toy x86 assembler and Lisp compiler that hasn’t seen new development since 2014. But before that, I had [blogged][1] [about it][2] and even [talked about it][3] at EuroClojure one time.

Over the week, I’ve re-read the [paper][4] that I’ve been loosely following while developing Lithium. In it, Abdulaziz Ghuloum advocates to have a testing infrastructure from day one, so that one can ensure that the compiler continues to work after each small modification. I’d cut corners on it before, but today, I’ve finally added one.

What’s the big deal? And why not earlier?

One of the original goals that I set myself for Lithium is that it have no runtime dependencies. Not even a C library; not even an OS. It produces raw x86 binaries targetting real mode – non-relocatable blobs of raw machine code. I’m running them in DOSBox, because it’s convenient, but the point is it’s not necessary.

(Some day, I’ll write a mission statement to explain why. But that’s a story for another day.)

And because the setup is so minimalistic, the setup suggested by Ghuloum becomes unfeasible. Ghuloum presupposes the existence of a host C compiler and linker; I have no such privilege. By itself, Lithium can barely output stuff to screen. There’s a `write-char` primitive that emits one character, but nothing more than that. And there’s as yet no library to add things to, because there’s no `defn` and not much of a global environment.

So what to do? I thought about the invariant in Ghuloum’s design, one that Lithium inherits as well:

_Every expression is compiled to machine code that puts its value in the `AX` register._

If I could somehow obtain the values that the CPU registers have at the end of executing a Lithium-compiled program, then I could compare them to the expected value in a test. But how to grab those registers?

That turned out to be easier than expected. Instead of extending Lithium to support printing decimal or hexadecimal numbers, I just grabbed [some pre-existing assembly code][5] to affix to the program as an epilog. (It does depend on DOS’s interrupt `21h`, but hey, it doesn’t hurt to have it for debugging/testing only.) Surprise: the snippet failed to compile, because Lithium’s assembler is woefully incomplete! But it was easy enough to extend it until it worked.

So this gave me a way to view the program’s results.

<img src="/img/blog/lithium-testing.png">

But there’s another problem: these results are printed within DOSBox. In the emulated DOS machine. I needed a way to transfer them back to the host. Can you guess how?

Yes, you’re right: the simplest thing (DOS redirection to a file, as in `PROG.COM >REG.TXT`) works. And you’ll laugh at me that it hasn’t occurred to me until now, when I’m writing up the [commit][6] that’s already out in the wild. Another proof that it pays to write documentation.

My original idea was… SCREEN CAPTURE!

I’ve scavenged Google for a DOS screen grabber that can produce text files and is not a TSR, [found one][7], bundled it with Lithium, and wrote [some duct-tape code][8] that invokes the compiled program and the screen grabber in turn and then parses the output. With that, I can finally have [tests][9] that check whether `(+ 3 4)` is really `7`.

And now let me go refactor it…

 [1]: /2012/05/14/lithium/
 [2]: /2013/05/26/lithium-revisited/
 [3]: https://danieljanus.pl/talks/reveal.js/2013-euroclojure.html#/
 [4]: http://scheme2006.cs.uchicago.edu/11-ghuloum.pdf
 [5]: http://www.fysnet.net/yourhelp.htm
 [6]: https://github.com/nathell/lithium/commit/27563b3c5b92f32b24f750d98248d013f924a700
 [7]: http://www.pc-tools.net/dos/dosutils/
 [8]: https://github.com/nathell/lithium/blob/27563b3c5b92f32b24f750d98248d013f924a700/src/lithium/driver.clj#L23-L36
 [9]: https://github.com/nathell/lithium/blob/27563b3c5b92f32b24f750d98248d013f924a700/test/lithium/compiler_test.clj
