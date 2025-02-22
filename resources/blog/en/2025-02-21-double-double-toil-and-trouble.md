---
date: 2025-02-21
title: Double, double toil and trouble
subtitle: or, Corner-Cases of Comparing Clojure Numbers
categories: programming clojure wat
---

[Let’s talk about][1] Clojure.

 [1]: https://www.destroyallsoftware.com/talks/wat

In Clojure, comparing two numbers can throw an exception.

<img src="/img/blog/wat-shark.jpg" alt="Wat">

Check this out:

```clojure
(< 1/4 0.5M)
;=> true        ; as expected

(< 1/3 0.5M)
; Execution error (ArithmeticException) at java.math.BigDecimal/divide (BigDecimal.java:1783).
; Non-terminating decimal expansion; no exact representable decimal result.
```

But why? Why would comparing two perfectly cromulent numbers throw an `ArithmeticException`?! Everybody knows that ⅓ < 0.5 – we aren’t dividing by zero or anything like that, are we?

Well, the problem is that we’re comparing a ratio to a `BigDecimal` (a decimal number of arbitrary precision). Java doesn’t offer a built-in way of comparing these (Clojure’s ratios aren’t part of the Java standard library), so it has to coerce one into the other. It chooses to coerce the ratio into a BigDecimal, so divides `(bigdec 1)` by `(bigdec 3)`…

…and that [throws!][2] The decimal representation of ⅓ is infinite, so you can’t keep all the digits in finite memory.

 [2]: https://docs.oracle.com/javase/8/docs/api/java/math/BigDecimal.html#divide-java.math.BigDecimal-

You may ask: how exactly does Clojure know what coercions to apply and how to produce the result? Let’s look at the code.

The implementation of `clojure.core/<` calls the Java method `clojure.lang.Numbers.lt`, which is implemented [like this][3]:

```java
static public boolean lt(Object x, Object y){
	return ops(x).combine(ops(y)).lt((Number)x, (Number)y);
}
```

 [3]: https://github.com/clojure/clojure/blob/clojure-1.12.0/src/jvm/clojure/lang/Numbers.java#L252-L254

What’s `ops`? It’s an implementation of the `Ops` interface, which has methods for addition, subtraction, etc.; each number class has its own implementation: there is a `LongOps`, `RatioOps`, `BigDecimalOps` etc.

The `combine` method can alter the behaviour of an `Ops` depending on the type of the other argument – for example, `RatioOps` switches to `BigDecimalOps` if the other argument is a `BigDecimal`. It’s like a poor man’s implementation of multiple dispatch, which Java doesn’t have.

`BigDecimalOps.lt` calls `toBigDecimal` on both arguments, and it’s [that method][4] that performs the failing division:

 [4]: https://github.com/clojure/clojure/blob/clojure-1.12.0/src/jvm/clojure/lang/Numbers.java#L297-L322

```java
static BigDecimal toBigDecimal(Object x) {
    // ... other cases ...
    if (x instanceof Ratio) {
        Ratio r = (Ratio)x;
        return (BigDecimal)divide(new BigDecimal(r.numerator), r.denominator);
    }
}
```

Incidentally, this used to produce the expected result in Clojure up to 1.2.1. At that version, Clojure already used the `Ops`-based multiple dispatch, but combining `RatioOps` with `BigDecimalOps` would yield the former, not the latter.

Is the current behaviour a bug? I’m not sure. It seems so, but maybe 1.3.0’s optimizations warrant this behaviour in the admitedly rare case. There’s an [ongoing discussion][4a] on the Ask Clojure Q&A.

 [4a]: https://ask.clojure.org/index.php/14411/comparing-ratios-with-bigdecimals-can-throw

So, in current Clojure, how do you compare ratios to bigdecs? Simple, you think: just coerce the bigdec to a double!

```clojure
(< 1/3 (double 0.5M))
;=> true

(> 2/3 (double 0.5M))
;=> true

(= 1/2 (double 0.5M))
;=> false
```

Wait, WHAT?

<img src="/img/blog/wat-cat.jpg" alt="Wat">

Yep. Comparing ratios to doubles for _inequality_ works fine, but a ratio is never _equal_ to a double (nor a bigdec), even if said double is an exact representation of the ratio.

This one is documented, but often forgotten about (and not hinted at by the docstring). From Clojure’s [equality guide][5]:

 [5]: https://clojure.org/guides/equality

> Clojure’s `=` is true when called with two immutable scalar values, if:
> - Both arguments are nil, true, false, the same character, or the same string (i.e. the same sequence of characters).
> - Both arguments are symbols, or both keywords, with equal namespaces and names.
> - Both arguments are numbers in the same 'category', and numerically the same, where category is one of:
>    - integer or ratio
>    - floating point (float or double)
>    - BigDecimal.

And indeed, the code for `Numbers.equal` has [a check for both operands’ categories][6] before it delves to the `Ops` business that we’ve seen. Remember also that Clojure has a numbers-only `==` which doesn’t trigger that category check:

```clojure
(== 1/2 (double 0.5M))
;=> true ; yay
```

Corollary: if you want to compare a ratio to a `BigDecimal`, you _could_ coerce the bigdec to a double. That can return an incorrect result only in a very narrow range of cases: when the BigDecimal’s value is close enough to the ratio that it would be lost in the double conversion.

For 100% certainty, the only way I’m aware of is to remember to always use `==` when comparing for equality, and explicitly coerce the bigdec to ratio:

```clojure
(defn exactly-equals? [ratio bigdec]
  (== (* 1 (clojure.lang.Numbers/toRatio bigdec)) ratio))

(exactly-equals? 1/18446744073709551616 5.42101086242752217003726400434970855712890625E-20M)
;=> true ; correct even in this pathological case!
```

(Multiplying by 1 forces Clojure to normalize the ratio. Otherwise, converting `0.5M` would have yielded `5/10` which doesn’t test `==` to `1/2`. Go figure.)

[6]: https://github.com/clojure/clojure/blob/clojure-1.12.0/src/jvm/clojure/lang/Numbers.java#L247-L250
