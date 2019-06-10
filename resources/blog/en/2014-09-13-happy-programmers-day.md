---
date: 2014-09-13
title: Happy Programmers’ Day!
categories: Haskell Z-machine programmers-day programming
---

Happy [Programmers’ Day][1], everyone!

A feast isn’t a feast, though, until it has a proper way of celebrating it. The [Pi Day][2], for instance, has one: you eat a pie (preferably exactly at 1:59:26.535am), but I haven’t heard of any way of celebrating the Programmers’ Day, so I had to invent one. An obvious way would be to write a program, preferably a non-trivial one, but that requires time and dedication, which not everyone is able to readily spare.

So here’s my idea: on Programmers’ Day, dust off a program that you wrote some time ago — something that is just lying around in some far corner of your hard disk, that you haven’t looked at in years, but that you had fun writing — and put it on [GitHub][3] for all the world to see, to share the joy of programming.

Let me initialize the new tradition by doing this myself. Here’s [HAZE][4], the Haskellish Abominable Z-machine Emulator. It was my final assignment for a course in Advanced Functional Programming, in my fourth year at the Uni, way back in 2004. It is an emulator for an ancient kind of virtual machine, the [Z-machine][5], written from scratch in Haskell. It allows you to play text adventure games, such as [Zork][6], much in the vein of [Frotz][7]. It’s not very complete, and supports versions of the Z-machine up to 3 only, so newer games won’t run on it as it stands, but Zork is playable.

It probably won’t even compile in modern Haskell systems: it was originally written for GHC version 6.2.1, and extensively uses the FiniteMap data type, which was obsoleted shortly after and is no longer found in modern systems. I should have Linux and Windows binaries lying around (yes, I had compiled it under Windows, using MinGW/PDCurses); I’ll put them on GitHub once I find them.

My mind now wanders ten years back in time, to the days when I was writing it. It took me about three summer weeks to write HAZE from scratch, most of that time on a slow laptop where it took quite a lot of seconds to get GHC to compile even a simple thing. I would do some of it differently if I were doing it now — for one, the state of a `ZMachine` is a central datatype to HAZE, and you’ll find a lot of functions that take and return ZMachines, so a state monad is an obvious choice; I didn’t understand monads well enough back then. But I still remember how I had the framework in place already and I was adding implementations of Z-code opcodes, one by one, to `ZMachine/ZCode/Impl.hs`, recompiling, rerunning, getting messages about unimplemented opcodes, when all of a sudden I got the familiar message about a white house and a small mailbox. Freude!

I hope you enjoy looking at it at least half as much as I had enjoyed writing it.

 [1]: https://en.wikipedia.org/wiki/Programmers'_Day
 [2]: https://en.wikipedia.org/wiki/Pi_Day
 [3]: https://github.com/
 [4]: https://github.com/nathell/haze
 [5]: https://en.wikipedia.org/wiki/Z-machine
 [6]: https://en.wikipedia.org/wiki/Zork
 [7]: https://davidgriffith.gitlab.io/frotz/