---
title: I made a website to guess tomorrow’s number of COVID-19 cases, and here’s what happened
date: 2020-11-08
categories: Clojure programming
---

## Before

It seems so obvious in hindsight. Here in Poland, people have been guessing it ever since the pandemic breakout: in private conversations, in random threads on social media, in comments under governmental information outlets. It seemed a matter of time before someone came up with something like this. In fact, on one Sunday evening in October, I found myself flabbergasted that apparently no one yet has.

I doled out $4 for a domain, [koronalotek.pl](http://koronalotek.pl) (can be translated as “coronalotto” or “coronalottery” – occurrences of the name on Twitter date back at least as far as April), and fired up a REPL. A few hours and 250 Clojure LOCs later, the site was up.

I wanted it to be as simple as possible. A form with two fields: “your name” and “how many cases tomorrow?” A top-ten list of today’s winners, sorted by the absolute difference between the guess and the actual number of cases, as [reported daily on Twitter](https://twitter.com/mz_gov_pl) by the Polish Ministry of Health. The official number, prominently displayed. And that’s all.

<img src="/img/blog/koronalotek.png">

On 17 October, I posted the link on my Facebook and Twitter feeds, and waited. The stream of guesses started to trickle in.

## After

It never grew to be more than a stream, but it hasn’t gone completely unnoticed either.

<img src="/img/blog/koronalotek-g1.png">

The above plot shows daily number of accepted guesses (i.e., those that were used to generate the next day’s winners) over time – a metric of popularity. Each day’s number means guesses cast in the 24 hours up until 10:30 (Warsaw time) on that day, which is when the official numbers are published by the Ministry of Health.

I’ve been filtering out automated submissions, as well as excess manual submissions by the same IP that seemed to skew the results too much – I’ve arbitrarily set the “excess” threshold at 10. The missing datapoint for 19 October is not a zero, but a N/A: I’ve lost that datapoint due to a glitch. More on this below.

The interest peaked on October 23, with more than a thousand guesses for that day (I think it was reposted by someone with a significant outreach back then), and has been slowly declining since.

I have privately received some feedback. One person has pointed out that they found the site distasteful and that making fun of pandemic tragedies made them uncomfortable. (I empathise; for me it’s not so much making fun as it is a coping mechanism—a way to put distance between my thoughts and the difficult times we’re in and to keep fears at bay.) Some people, however, have thanked me for making them smile when they guessed more or less correctly.

Back to data. Being a data junkie, I looked at what I had been collecting. First things first: how accurate is the collective predictive power of the guessers?

<img src="/img/blog/koronalotek-g2.png">

Quite accurate, in fact! Data for this plot has only been slightly preprocessed, by filtering out “unreasonable” guesses that don’t fall within the range `[100; 50000]`.

People have over- and underguesstimated the number of new cases, but not by much. There were only a few occasions where the actual case count didn’t fall within one standard deviation of the mean of guesses (represented by the whiskers around blue bars on the plot). Granted, the daily standard deviation tends to be large (on the order of a few thousand), but still, I’m impressed. A paper on estimating the growth of pandemic based on coronalottery results coming soon to a journal near you! ;-)

Just for the heck of it, I’ve also been looking at individual votes. Specifically, names. Here’s a snapshot of unique guessers’ names sorted by decreasing length, on 23 October. (NSFW warning: expletives ahead!)

<img src="/img/blog/koronalotek-names.jpg">

Let me translate a few of these for those of you who don’t speak Polish:

1 is “Sasin has fucked over 70 million zlotys for elections that didn’t take place and was never held responsible.” This alludes to the [ghost election in Poland](https://notesfrompoland.com/2020/05/27/70-million-zloty-bill-for-polands-abandoned-presidential-election/) from May. This news had gone memetic, going so far as Minister Sasin’s name being ironically used as a dimensionless unit of 70 million (think Avogadro’s number). You’ll discover the same theme in #2, #3, #5, and others.

6 is “CT {Constitutional Tribunal}, you focking botch, stop repressing my abortion”. Just a day before, the Polish constitutional court (whose current legality is [disputed at best][1]) has [decreed a ban on almost all legal abortion][2] in Poland, giving rise to [the biggest street protests in decades][3].

[1]: https://en.wikipedia.org/wiki/Constitutional_Tribunal_(Poland)#2015%E2%80%93present:_Polish_Constitutional_Court_crisis
[2]: https://notesfrompoland.com/2020/10/22/constitutional-court-ruling-ends-almost-all-legal-abortion-in-poland/
[3]: https://edition.cnn.com/2020/10/31/europe/poland-abortion-protests-scli-intl/index.html

Not all is political: 4 is “Why study for the exam if we’re not gonna survive until November anyway?”. I hope whoever wrote this is alive and well.

Corollary? Give people a text field, and they’ll use it to express themselves: politically or otherwise.

In fact, I have taken the liberty of chiming in. Shortly after, I altered the thank-you page (which used to just say “thanks for guessing”) to proudly display one of the emblems of the Women’s Strike, along with a link to a [crowdfounding campaign][4] for an NGO that supports women needing abortion.

[4]: https://zrzutka.pl/kasa-na-aborcyjny-dream-team-55g5gx

<img src="/img/blog/koronalotek-thanks.jpg">

## Inside out

I’m not much of a DevOps person, so I deployed it the quick and dirty way, not caring about scalability or performance. The maxim “make it as simple as possible” permeates the setup.

I just started a REPL within a `screen` session on the tiny Scaleway C1 server that also hosts this blog and some of my other personal stuff. I launched a Jetty server within it, and set up a nginx proxy. And that’s pretty much it. I liberally tinker with the app’s state in “production,” evaluating all kinds of expressions when I feel like it.

Code changes are deployed by `git pull`ing new developments and doing `(require 'koronalotek.core :reload)` in the REPL.

Someone tried a SQL injection attack. This is doomed to fail because there’s no SQL involved. In fact, there’s no database at all. The entire state is kept in an in-memory atom and periodically synced out to an EDN file. In addition, state is reset and archived daily at the time of announcing winners. (I’ve added the archiving after forgetting it on one occasion – hence the lack of data for 19 October.)

I also don’t yet have a mechanism of automatically pulling in the Ministry of Health’s data. Every morning, I spend two minutes checking if there’s excess automatic votes, removing them if any, and then filling in the blanks:

```clojure
(new-data! #inst "2020-11-08T10:30+01:00" 24785)
```

For all the violations of good practices in this setup, it has worked out surprisingly well so far. I’ve resorted to removing automated votes a handful of times, and blacklisting IPs of voting bots in the nginx setup twice, but otherwise it’s been a low-maintenance toy. People seem to be willing to have fun, and I’m just not interfering.

## Takeaways

1. You should call on your country’s authorities to exert pressure on the Polish government to respect women’s choices and stop actively repressing them.
2. Give people a text field, and they’ll use it to express themselves.
3. Release early, release often.
