---
title: "Combining virtual sequences<br>or, Sequential Fun with Macros<br>or, How to Implement Clojure-Like Pseudo-Sequences with Poor Man’s Laziness in a Predominantly Imperative Language"
date: 2011-12-09
categories: Clojure Lisp programming
---

## Sequences and iteration

There are a number of motivations for this post. One stems from my extensive exposure to Clojure over the past few years: this was, and still is, my primary programming language for everyday work. Soon, I realized that much of the power of Clojure comes from a _sequence_ abstraction being one of its central concepts, and a standard library that contains many sequence-manipulating functions. It turns out that by combining them it is possible to solve a wide range of problems in a concise, high-level way. In contrast, it pays to think in terms of whole sequences, rather than individual elements.

Another motivation comes from a classical piece of functional programming humour, [The Evolution of a Haskell Programmer][1]. If you don’t know it, go check it out: it consists of several Haskell implementations of factorial, starting out from a straightforward recursive definition, passing through absolutely hilarious versions involving category-theoretical concepts, and finally arriving at this simple version that is considered most idiomatic:

```haskell
fac n = product [1..n]
```

This is very Clojure-like in that it involves a sequence (a list comprehension). In Clojure, this could be implemented as

```clojure
(defn fac [n]
  (reduce * 1 (range 1 (inc n)))
```

Now, I thought to myself, how would I write factorial in an imperative language? Say, Pascal?

```pascal
function fac(n : integer) : integer;
var
  i, res : integer;
begin
  res := 1;
  for i := 1 to n do
    res := res * i;
  fac := res;
end;
```

This is very different from the functional version that works with sequences. It is much more elaborate, introducing an explicit loop. On the other hand, it’s memory efficient: it’s clear that its memory requirements are O(1), whereas a naïve implementation of a sequence would need O(n) to construct it all in memory and then reduce it down to a single value.

Or is it really that different? Think of the changing values of `i` in that loop. On first iteration it is 1, on second iteration it’s 2, and so on up to n. Therefore, one can really think of a `for` loop as a sequence! I call it a “virtual” sequence, since it is not an actual data structure; it’s just a snippet of code.

To rephrase it as a definition: a virtual sequence is a snippet of code that (presumably repeatedly) _yields_ the member values.

## Let’s write some code!

To illustrate it, throughout the remainder of this article I will be using Common Lisp, for the following reasons:

- It allows for imperative style, including GOTO-like statements. This will enable us to generate very low-level code.
- Thanks to macros, we will be able to obtain interesting transformations.

Okay, so let’s have a look at how to generate a one-element sequence. Simple enough:

```lisp
(defmacro vsingle (x)
 `(yield ,x))
```

The name `VSINGLE` stands for “Virtual sequence that just yields a SINGLE element”. (In general, I will try to define virtual sequences named and performing similarly to their Clojure counterparts here; whenever there is a name clash with an already existing CL function, the name will be prefixed with `V`.) We will not concern ourselves with the actual definition of `YIELD` at the moment; for debugging, we can define it just as printing the value to the standard output.

```lisp
(defun yield (x)
  (format t "~A~%" x))
```

We can also convert a Lisp list to a virtual sequence which just yields each element of the list in turn:

```lisp
(defmacro vseq (list)
  `(loop for x in ,list do (yield x)))

(defmacro vlist (&rest elems)
  `(vseq (list ,@elems)))
```

Now let’s try to define `RANGE`. We could use `loop`, but for the sake of example, let’s pretend that it doesn’t exist and write a macro that expands to low-level GOTO-ridden code. For those of you who are not familiar with Common Lisp, `GO` is like GOTO, except it takes a label that should be established within a `TAGBODY` container.

```lisp
(defmacro range (start &optional end (step 1))
  (unless end
    (setf end start start 0))
  (let ((fv (gensym)))
    `(let ((,fv ,start))
       (tagbody
        loop
          (when (>= ,fv ,end)
            (go out))
          (yield ,fv)
          (incf ,fv ,step)
          (go loop)
       out))))
```

_Infinite_ virtual sequences are also possible. After all, there’s nothing preventing us from considering a snippet of code that loops infinitely, executing `YIELD`, as a virtual sequence! We will define the equivalent of Clojure’s iterate: given a function `fun` and initial value `val`, it will repeatedly generate `val`, `(fun val)`, `(fun (fun val))`, etc.

```lisp
(defmacro iterate (fun val)
  (let ((fv (gensym)))
    `(let ((,fv ,val))
       (tagbody loop
          (yield ,fv)
          (setf ,fv (funcall ,fun ,fv))
          (go loop)))))
```

So far, we have defined a number of ways to create virtual sequences. Now let’s ask ourselves: is there a way, given code for a virtual sequence, to yield only the elements from the original that satisfy a certain predicate? In other words, can we define a `filter` for virtual sequences? Sure enough. Just replace every occurrence of `yield` with code that checks whether the yielded value satisfies the predicate, and only if it does invokes `yield`.

First we write a simple code walker that applies some transformation to every `yield` occurrence in a given snippet:

```lisp
(defun replace-yield (tree replace)
  (if (consp tree)
      (if (eql (car tree) 'yield)
          (funcall replace (cadr tree))
          (loop for x in tree collect (replace-yield x replace)))
      tree))
```

We can now write `filter` like this:

```lisp
(defmacro filter (pred vseq &environment env)
  (replace-yield (macroexpand vseq env)
                 (lambda (x) `(when (funcall ,pred ,x) (yield ,x)))))
```

It is important to point out that since `filter` is a macro, the arguments are passed to it unevaluated, so if `vseq` is a virtual sequence definition like `(range 10)`, we need to macroexpand it before replacing `yield`.

We can now verify that `(filter #'evenp (range 10))` works. It macroexpands to something similar to

```lisp
(LET ((#:G70192 0))
  (TAGBODY
    LOOP (IF (>= #:G70192 10)
           (PROGN (GO OUT)))
         (IF (FUNCALL #'EVENP #:G70192)
           (PROGN (YIELD #:G70192)))
         (SETQ #:G70192 (+ #:G70192 1))
         (GO LOOP)
    OUT))
```

`concat` is extremely simple. To produce all elements of `vseq1` followed by all elements of `vseq2`, just execute code corresponding to `vseq1` and then code corresponding to `vseq2`. Or, for multiple sequences:

```lisp
(defmacro concat (&rest vseqs)
  `(progn ,@vseqs))
```

To define `take`, we’ll need to wrap the original code in a block that can be escaped from by means of `return-from` (which is just another form of `goto`). We’ll add a counter that will start from `n` and keep decreasing on each `yield`; once it reaches zero, we escape the block:

```lisp
(defmacro take (n vseq &environment env)
  (let ((x (gensym))
        (b (gensym)))
    `(let ((,x ,n))
       (block ,b
         ,(replace-yield (macroexpand vseq env)
                         (lambda (y) `(progn (yield ,y)
                                             (decf ,x)
                                             (when (zerop ,x)
                                               (return-from ,b)))))))))
```

`rest` (or, rather, `vrest`, as that name is taken) can be defined similarly:

```lisp
(defmacro vrest (vseq &environment env)
  (let ((skipped (gensym)))
    (replace-yield
     `(let ((,skipped nil)) ,(macroexpand vseq env))
     (lambda (x) `(if ,skipped (yield ,x) (setf ,skipped t))))))
```

`vfirst` is another matter. It should return a value instead of producing a virtual sequence, so we need to actually execute the code — but with `yield` bound to something else. We want to establish a block as with `take`, but our `yield` will immediately return from the block once the first value is yielded:

```lisp
(defmacro vfirst (vseq)
  (let ((block-name (gensym)))
   `(block ,block-name
      (flet ((yield (x) (return-from ,block-name x)))
        ,vseq))))
```

Note that so far we’ve seen three classes of macros:

- macros that create virtual sequences;
- macros that transform virtual sequences to another virtual sequences;
- and finally, vfirst is our first example of a macro that produces a result out of a virtual sequence.

Our next logical step is `vreduce`. Again, we’ll produce code that rebinds `yield`: this time to a function that replaces the value of a variable (the accumulator) by result of calling a function on the accumulator’s old value and the value being yielded.

```lisp
(defmacro vreduce (f val vseq)
  `(let ((accu ,val))
     (flet ((yield (x) (setf accu (funcall ,f accu x))))
       ,vseq
       accu)))
```

We can now build a constructs that executes a virtual sequence and wraps the results up as a Lisp list, in terms of `vreduce`.

```lisp
(defun conj (x y)
  (cons y x))

(defmacro realize (vseq)
 `(nreverse (vreduce #'conj nil ,vseq)))
```

Let’s verify that it works:

```lisp
CL-USER> (realize (range 10))
(0 1 2 3 4 5 6 7 8 9)

CL-USER> (realize (take 5 (filter #'oddp (iterate #'1+ 0))))
(1 3 5 7 9)
```

Hey! Did we just manipulate an _infinite_ sequence and got the result in a _finite_ amount of time? And that without explicit support for laziness in our language? How cool is that?!

Anyway, let’s finally define our factorial:

```lisp
(defun fac (n)
  (vreduce #'* 1 (range 1 (1+ n))))
```

## Benchmarking

Factorials grow too fast, so for the purpose of benchmarking let’s write a function that adds numbers from 0 below n, in sequence-y style. First using Common Lisp builtins:

```lisp
(defun sum-below (n)
  (reduce #'+ (loop for i from 0 below n collect i) :initial-value 0))
```

And now with our virtual sequences:

```lisp
(defun sum-below-2 (n)
  (vreduce #'+ 0 (range n)))
```

Let’s try to time the two versions. On my Mac running Clozure CL 1.7, this gives:

```lisp
CL-USER> (time (sum-below 10000000))
(SUM-BELOW 10000000) took 8,545,512 microseconds (8.545512 seconds) to run
                    with 2 available CPU cores.
During that period, 2,367,207 microseconds (2.367207 seconds) were spent in user mode
                    270,481 microseconds (0.270481 seconds) were spent in system mode
5,906,274 microseconds (5.906274 seconds) was spent in GC.
 160,000,016 bytes of memory allocated.
 39,479 minor page faults, 1,359 major page faults, 0 swaps.
49999995000000

CL-USER> (time (sum-below-2 10000000))
(SUM-BELOW-2 10000000) took 123,081 microseconds (0.123081 seconds) to run
                    with 2 available CPU cores.
During that period, 127,632 microseconds (0.127632 seconds) were spent in user mode
                    666 microseconds (0.000666 seconds) were spent in system mode
 4 minor page faults, 0 major page faults, 0 swaps.
49999995000000
```

As expected, `SUM-BELOW-2` is much faster, causes less page faults and presumably conses less. (Critics will be quick to point out that we could idiomatically write it using `LOOP`’s `SUM/SUMMING` clause, which would probably be yet faster, and I agree; yet if we were reducing by something other than `+` — something that `LOOP` has not built in as a clause — this would not be an option.)

## Conclusion

We have seen how snippets of code can be viewed as sequences and how to combine them to produce other virtual sequences. As we are nearing the end of this article, it is perhaps fitting to ask: what are the limitations and drawbacks of this approach?

Clearly, this kind of sequences is less powerful than “ordinary” sequences such as Clojure’s. The fact that we’ve built them on macros means that once we escape the world of code transformation by invoking some macro of the third class, we can’t manipulate them anymore. In Clojure world, `first` and `rest` are very similar; in virtual sequences, they are altogether different: they belong to different worlds. The same goes for `map` (had we defined one) and `reduce`.

But imagine that instead of having just one programming language, we have a high-level language A in which we are writing macros that expand to code in a low-level language B. It is important to point out that the generated code is very low-level. It could almost be assembly: in fact, most of the macros we’ve written don’t even require language B to have composite data-types beyond the type of elements of collections (which could be simple integers)!

Is there a practical side to this? I don’t know: to me it just seems to be something with hack value. Time will tell if I can put it to good use.
