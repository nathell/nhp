---
title: The TeX Hackery
date: 2008-04-06
categories: LaTeX TeX
---

After a longish while of inactivity, I finally got around to finishing the draft spec of a next-generation protocol for [Poliqarp][1], the be-all-end-all corpus concordance tool that I maintain. The spec is being written in LaTeX, and it has a number of subsections that describe particular methods of the protocol. Each one of those is further divided into sub-subsections that describe the method’s signature, purpose, syntax of request, syntax of response, and an optional example. I thought to write a couple of macros to help me separate the document’s logic from details of formatting, so that I could say:

```latex
\synopsis/() -> {version : int; extensions : string*}/
```
and have it expanded into:

```latex
\paragraph{Synopsis}
\verb/{version : int; extensions : string*}/
```

Being a casual LaTeX user who hardly ever writes his own macros, I first thought to use LaTeX’s command-defining commands, `\newcommand` and `\renewcommand`. However, I quickly ran into the limitation that the argument of commands defined in such a way can only be delimited by curly braces, which I could not use because they might appear in the argument itself.

I googled around and found that this limitation can be overcome by using `\def` instead, which is not a LaTeX macro but rather an incantation of plain TeX, and allows to use arbitrary syntax for delimiting arguments. Having found that, my first shot was:

```latex
\def\synopsis/#1/{\paragraph{Synopsis}\verb/#1/}
```

which, obviously enough, turned out not to work, producing errors about `\verb` ended by an end-of-line.

“What the heck?” I thought, and resorted to Google again, this time searching for tex macros expanding to verb. This yielded an entry from some TeX FAQ, which basically states that the `\verb` is a “fragile” command, and as such it cannot appear in bodies of macros. Ook. So it can’t be done?

“But,” I thought, “TeX is such a flexible and powerful tool, there must be some way around this!” And, as it would turn out, there is. Yet more googling led me to [this thread][2] on comp.text.tex, where someone gives the following answer for a similar question:

```latex
\def\term#{ %
   \afterassignment\Term \let\TErm= }%

\edef\Term{\noexpand\verb \string}}
```

Now this is overkill. Why in the world am I forced to stuff such incomprehensible hackery into my document just to perform a seemingly simple task?! Easy things should be easy — that’s one of the principles of good design.

Reluctantly, I copied it over, and attempted to adjust it to my needs. After a number of initial failed attempts, I thought that I might actually attempt to understand what all these `\afterassignment`’s, `\noexpand`’s and `\edef`’s are for, so I downloaded the [TeXbook][3] and dived straight in.

I spent another fifteen minutes or so reading bits of it and trying to understand tokens, macros, when they are expanded and when merely carried over, etc. But a sparkle of thought made me replace the whole complicated thingy with a simple snippet that actually worked.

```latex
\def\synopsis{\paragraph{Synopsis}\verb}
```

That’s right. This superficially resembles a C preprocessor macro, and works because I was lucky enough to have `\verb` appear last in the definition, thus allowing the “arguments” of `\synopsis` to be specified just like arguments to `\verb` and fit at exactly right place. I’m almost certain that it does not always work this way, but for now it’ll suffice.

Oh well. TeX is undoubtedly a fine piece of software that provides splendid results if used right. But I can’t get over the impressions that there are a great deal more idiosyncracies like this in it than in, say, Common Lisp, even though the latter’s heritage tracks back to as early as 1958 and is a whopping twenty years longer than TeX’s. (On the side note, as it turns out, someone has already written [a Lisp-based preprocessor for TeX macros][4]. Gotta check it out someday.)

As for the TeXbook itself: it is a fine piece of documentation that I will definitely have to add to my must-read list, though it admittedly has a math-textbookish feel to it. First, however, I want to finish “Shaman’s Crossing” by Robin Hobb (which I will probably brag about in a separate post once I’m finished with it) and tackle Christian Queinnec’s “Lisp in Small Pieces”.

 [1]: http://poliqarp.sf.net/
 [2]: http://groups.google.pl/group/comp.text.tex/browse_thread/thread/5bca05fb8865a9c2
 [3]: http://www-cs-faculty.stanford.edu/~knuth/abcde.html
 [4]: http://www3.interscience.wiley.com/cgi-bin/abstract/98518913/ABSTRACT
