---
date: 2008-06-23
title: "cl-morfeusz: A ninety minutes’ hack"
categories: Lisp programming
---

Here’s what I came up with today, after no more than 90 minutes of coding (complete with comments and all):

```nohighlight
MORFEUSZ> (morfeusz-analyse "zażółć gęślą jaźń")
((0 1 "zażółć" "zażółcić" "impt:sg:sec:perf")
 (1 2 "gęślą" "gęśl" "subst:sg:inst:f")
 (2 3 "jaźń" "jaźń" "subst:sg:nom.acc:f"))
```

This is [cl-morfeusz][1] in action, a Common Lisp interface to [Morfeusz][2], the morphological analyser for Polish.

It’s a single Lisp file, so there’s no ASDF system definition or asdf-installability for now. I’m not putting it under version control, either. Or, should I say, not yet. When I get around to it, I plan to write a simple parser and write a Polish-language version of [the text adventure that started it all][3].

Meanwhile, you may use cl-morfeusz for anything you wish (of course, as long as you comply with Morfeusz’s license). Have fun!

_Update 2010-Jan-17_: With the advent of UTF-8 support in CFFI, the ugly workarounds in the code are probably no longer necessary; I don’t have time to check it right now, though.

 [1]: http://danieljanus.pl/code/morfeusz.lisp
 [2]: http://nlp.ipipan.waw.pl/~wolinski/morfeusz/
 [3]: http://en.wikipedia.org/wiki/Colossal_Cave_Adventure
