---
date: 2020-02-10
title: Indenting cond forms
categories: Lisp Clojure programming
---

Indentation matters when reading Clojure code. It is the primary visual cue that helps the reader discern the code structure. Most Clojure code seen in the wild conforms to either the [community style guide][1] or the proposed [simplified rules][2]; the existing editors make it easy to reformat code to match them.

I find both these rulesets to be helpful when reading code. But there’s one corner-case that’s been irking me: `cond` forms.

`cond` takes an even number of arguments: alternating test-expression pairs. They are commonly put next to each other, two forms per line.

```clojure
(cond
  test expr-1
  another-test expr-2
  :else expr-3)
```

Sometimes, people align the expressions under one another, in a tabular fashion:

```clojure
(cond
  test         expr-1
  another-test expr-2
  :else        expr-3)
```

But things get out of hand when either `tests` or `exprs` get longer and call for multiple lines themselves. There are several options here, all of them less than ideal.

### Tests and expressions next to each other

In other words, keep the above rule. Because we’ll have multiple lines in a form, this tends to make the resulting code axe-shaped:

```clojure
(cond
  (= (some-function something) expected-value) (do
                                                 (do-this)
                                                 (and-also-do-that))
  (another-predicate something-else) (try
                                       (do-another-thing)
                                       (catch Exception _
                                         (println "Whoops!"))))
```

This yields code that is indented abnormally far to the right, forcing the reader’s eyeballs to move in two dimensions – even more so if the tabular feel is desired. If _both_ the test and the expression is multi-lined, it just looks plain weird.

### Stack all forms vertically, no extra spacing

```clojure
(cond
  (= (some-function something) expected-value)
  (do
    (do-this)
    (and-also-do-that))
  (another-predicate something-else)
  (try
    (do-another-thing)
    (catch Exception _
      (println "Whoops!"))))
```

This gets rid of the long lines, but introduces another problem: it’s hard to tell at a glance

- where a given test or expression starts or ends;
- which tests are paired with which expression;
- whether a given line corresponds to a test or an expression, and which one.

### Stack all forms vertically, blank lines between test/expr pairs

```clojure
(cond
  (= (some-function something) expected-value)
  (do
    (do-this)
    (and-also-do-that))

  (another-predicate something-else)
  (try
    (do-another-thing)
    (catch Exception _
      (println "Whoops!"))))
```

The Style Guide [says][3] that this is an “ok-ish” thing to do.

But with the added blank lines, logical structure of the code is much more apparent. However, it breaks another assumption that I make when reading the code: _functions contain no blank lines._ The Style Guide even [mentions it][4], saying that `cond` forms are an acceptable exception.

It is now harder to tell at a glance where the enclosing function starts or ends. And once this assumption is broken once, the brain expects it to be broken again, causing reading disruption across the entire file.

### Forms one under another, extra indentation for expressions only

```clojure
(cond
  (= (some-function something) expected-value)
    (do
      (do-this)
      (and-also-do-that))
  (another-predicate something-else)
    (try
      (do-another-thing)
      (catch Exception _
        (println "Whoops!"))))
```

I resorted to this several times. The lines are not too long; the visual cues are there; it’s obvious what is the condition, what is the test, and what goes with what.

Except… it’s against the rules. List items stacked vertically should be aligned one under the other. I have to actively fight my Emacs to enforce this formatting, and it will be lost next time I press `C-M-q` on this form. No good.

### Forms one under another, expressions prefixed by `#_=>`

```clojure
(cond
  (= (some-function something) expected-value)
  #_=> (do
         (do-this)
         (and-also-do-that))
  (another-predicate something-else)
  #_=> (try
         (do-another-thing)
         (catch Exception _
           (println "Whoops!"))))
```

This one is my own invention: I haven’t seen it anywhere else. But I think it manages to avoid most problems.

`#_` is a reader macro that causes the next form to be elided and not seen by the compiler. `=>` is a valid form. Thus, `#_=>` is effectively whitespace as far as the compiler is concerned, and the indentation rules treat it as yet another symbol (although it technically isn’t one). No tooling is broken, no assumptions are broken, and the `#_=>` tends to be
syntax-highlighted unintrusively so it doesn’t stand out. I tend to read it aloud as “then.”

### Meanwhile, in another galaxy

Other Lisps (Scheme and CL) wrap each test/expression pair in an extra pair of parens, thereby avoiding the blending of conditions and expressions when indented one under the other.
But I’m still happy Clojure went with fewer parens. As I say, this is a corner case where additional pair of parens would somewhat help, but most of the time I find them less
aesthetic and a visual clutter.

 [1]: https://github.com/bbatsov/clojure-style-guide#source-code-layout-organization
 [2]: https://tonsky.me/blog/clojurefmt/
 [3]: https://github.com/bbatsov/clojure-style-guide#short-forms-in-cond
 [4]: https://github.com/bbatsov/clojure-style-guide#no-blank-lines-within-def-forms
