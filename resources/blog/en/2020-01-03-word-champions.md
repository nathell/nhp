---
date: 2020-01-03
title: Word Champions
categories: Clojure programming re-frame
---

This story begins on August 9, 2017, when a friend messaged me on Facebook: “Hey, I’m going to be on a TV talent show this weekend. They’ll be giving me this kind of problems. Any ideas how to prepare?”

He attached a link to this video:

<iframe width="100%" height="500" src="https://www.youtube.com/embed/34AcKyYdNBo" frameborder="0" allowfullscreen></iframe>

Now, we’re both avid Scrabble players, so we explored some ideas about extracting helpful data out of the [Official Polish Scrabble Player’s Dictionary][1]. I launched a Clojure REPL and wrote some throwaway code to generate sample training problems for Krzysztof. The code used a brute-force algorithm, so it was dog slow, but it was a start. It was Wednesday.

I woke up next morning with the problem still in my head. Clearly, I had found myself in a [nerd sniping][2] situation.

<img src="https://imgs.xkcd.com/comics/nerd_sniping.png">

There was only one obvious way out—to write a full-blown training app so that Krzysztof could practice as if he were in the studio. The clock was ticking: we had two days left.

After work, I started a fresh [re-frame][3] project. (I was a recent re-frame convert those days, so I wanted to see how well it could cope with the task at hand.) Late that night, or rather early next morning, the prototype was ready.

It had very messy code. It only worked on Chrome. It failed miserably on mobile. It took ages to load. It had native JS dependencies, notably [Material-UI][4] and [react-dnd][5], and for some reason it would not compile with ClojureScript’s advanced optimization turned on; so it weighed in at more than 6 MB, slurping in more than 300 JS files on load.

But it worked.

Krzysztof didn’t win his episode against the other contestants, ending up third, but he completed his challenge successfully. It took him 3 minutes and 42 seconds, out of 5 minutes allotted. The episode aired on 24 October.

<iframe width="100%" height="500" src="https://www.youtube.com/embed/7ec6j31nlAk" frameborder="0" allowfullscreen></iframe>

Krzysztof said that the problem he ended up solving on the show was way easier than the ones generated by the app: had they been more difficult, the wow factor might have been higher.

Several months later, we met at a Scrabble tournament, and I received a present. I wish I had photographed that bottle of wine, so I could show it here, but I hadn’t.

Meanwhile, the code remained messy and low-priority. But I kept returning to it when I felt like it, fixing up things one at a time. I’ve added difficulty levels, so you can have only one diagram, or three. I’ve made it work on Firefox. I’ve done a major rewrite, restructuring the code in a sane way and removing the JS dependencies other than React. I’ve made advanced compilation work, getting the JS down to 400K. I’ve made it work on mobile devices. I’ve written a puzzle generator in C, which ended up several orders of magnitude faster than the prototype Clojure version (it’s still brute-force, but uses some dirty C tricks to speed things up; I hope to rewrite it in Rust someday).

And now, 2½ years later, I’ve added an English version, with an accompanying set of puzzles (generated from a wordlist taken from [this repo][6]), for the English-speaking world to enjoy.

[Play Word Champions now!][7]

The code is [on GitHub][8] if you’d like to check it out or try hacking on it. It’s small, less than 1KLOC in total, so I think it can be a learning tool for re-frame or ClojureScript.

(This game as featured on the TV shows is called Gridlock. The name “Word Champions” was inspired by the title of Krzysztof’s video on YouTube, literally meaning “Lord of the Words”. There is no pun in the Polish title.)

 [1]: http://www.pfs.org.pl/english.php
 [2]: https://xkcd.com/356/
 [3]: https://github.com/day8/re-frame/
 [4]: https://material-ui.com/
 [5]: https://react-dnd.github.io/react-dnd/about
 [6]: https://github.com/first20hours/google-10000-english
 [7]: http://danieljanus.pl/wladcyslow/
 [8]: https://github.com/nathell/wordchampions
