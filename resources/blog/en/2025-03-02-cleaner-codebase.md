---
date: 2025-03-02
title: Cleaner codebase, happier mind
categories: programming
---

This is my home-office desk on a typical day. Yuck – look at those mugs, cables and rubbish!

<img src="/img/blog/my-desk.webp">

As a person with ADHD, I have a hard time maintaining cleanliness – and a high tolerance to mess around me. However, being in a cluttered environment does take its toll. Often I find myself frustrated by it, but also overwhelmed by tasks at hand, to the point of cleaning up feeling almost an insurmountable chore; often, when I start my workday by physically cleaning things up, I find it giving me a dopamine boost that impacts my productivity for the rest of the day.

I’m not alone. There is a [known link between office cleanliness and wellbeing][1]; some companies have clean desk policies. If nothing else, keeping the work environment clean has a positive psychological effect on people.

Increasingly often, I find myself wondering: why don’t we apply the same thinking to codebases?

Let me stress that I’m not talking about “clean code” in the [Uncle Bob sense][2]; I mean the chores that everyone would like to see done, but nobody apparently has time for doing — the cruft that has accumulated as tech debt. Every codebase has something like this, and you know it when you see it. That flaky test that’s been failing once in 20 times or so, for no apparent reason. That legacy component that could plausibly be implemented with more modern infrastructure. That Jenkins instance you keep around just for CI-ing it. Those three in-house libraries that all do the same thing, but in slightly different ways. Those modules that are only there because you ran an A/B test involving them a year ago, which has since been rolled back. And so on.

Yes, this is tech debt, and has to be managed economically. Sometimes it makes sense to bear with things as they are, because your time is needed elsewhere. Or there’s no clear financial gain to be had from investing effort in cleaning up that stuff.

But I believe there are _psychological_ gains. It can give a sense of accomplishment; it can make the codebase more pleasant to work with; it can reduce frustration. It can make people happier in the long run.

One of the strategies I’ve found useful for home cleaning is allocating regular but short time slots in the calendar. I call them “a quarter for home.” While 15 minutes is not enough for a thorough cleanup of a room, it can still make a night-and-day difference in how it feels to be in that room.

And so, going forward, I’m instituting the same policy for myself, for codebases I work with. A daily quarter for code. Or half an hour every second morning. I might be busy, but it happens very rarely that I don’t have a half an hour to spare. Sure, some cleanups might require multi-day refactoring to complete, but so what? There are smaller ones, requiring just one or a few sessions. The fact that I have a limited, dedicated time slot means that I’ll ask myself “how can I use it effectively?” And even larger undertakings can be done in multiple half-an-hour-long sittings: there are no deadlines.

Whether or not this works out remains to be seen (forming habits is another thing that ADHD makes harder). I plan to follow up with a retrospective post in a few months. In the meantime, if you have done or plan to do something similar, I’m keen to hear from you!

 [1]: https://psycnet.apa.org/record/2020-73700-008
 [2]: https://www.amazon.com/Clean-Code-Handbook-Software-Craftsmanship/dp/0132350882
