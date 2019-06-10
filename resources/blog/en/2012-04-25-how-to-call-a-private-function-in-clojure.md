---
date: 2012-04-25
title: How to call a private function in Clojure
categories: Clojure programming
---

**tl;dr:** Don’t do it. If you really have to, use `(#'other-library/private-function args)`.

<hr>

A private function in Clojure is one that has been defined using the `defn-` macro, or equivalently by setting the metadata key `:private` to `true` on the var that holds the function. It is normally not allowed in Clojure to call such functions from outside of the namespace where they have been defined. Trying to do so results in an `IllegalStateException` stating that the var is not public.

It is possible to circumvent this and call the private function, but it is not recommended. That the author of the library decided to make a function private probably means that he considers it to be an implementation detail, subject to change at any time, and that you should not rely on it being there. If you think it would be useful to have this functionality available as part of the public API, your best bet is to contact the library author and consult the change, so that it may be included officially in a future version.

Contacting the author, however, is not always feasible: she may not be available or you might be in haste. In this case, several workarounds are available. The simplest is to use `(#'other-library/private-function args)`, which works in Clojure 1.2.1 and 1.3.0 (it probably works in other versions of Clojure as well, but I haven’t checked that).

Why does this work? When the Clojure compiler encounters a form `(sym args)`, it invokes `analyzeSeq` on that form. If its first element is a symbol, it proceeds to analyze that symbol. One of the first operation in that analysis is checking if it names an inline function, by calling `isInline`. That function looks into the metadata of the Var named by the symbol in question. If it’s not public, it [throws an exception][1].

On the other hand, `#'` is the reader macro for var. So our workaround is equivalent to `((var other-library/private-function) args)`. In this case, the first element of the form is not a symbol, but a form that evaluates to a var. The compiler is not able to check for this so it does not insert a check for privateness. So the code compiles to calling a Var object.

Here’s the catch: Vars are callable, just like functions. They [implement `IFn`][2]. When a var is called, it delegates the call to the `IFn` object it is holding. This has been recently [discussed on the Clojure group][3]. Since that delegation does not check for the var’s privateness either, the net effect is that we are able to call a private function this way.

[1]: https://github.com/clojure/clojure/blob/clojure-1.3.0/src/jvm/clojure/lang/Compiler.java#L6281
 [2]: https://github.com/clojure/clojure/blob/clojure-1.3.0/src/jvm/clojure/lang/Var.java#L18
 [3]: https://groups.google.com/d/msg/clojure/1Su9o_8JZ8g/uZL-n4uRSiUJ
