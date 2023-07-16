---
title: The pitfalls of <code>lein swank</code>
date: 2010-03-31
categories: Clojure Leiningen programming
---

A couple of weeks ago I finally got around to acquainting myself with [Leiningen][1], one of the most popular build tools for Clojure. The thing that stopped me the most was that Leiningen uses [Maven][2] under the hood, which seemed a scary beast at first sight — but once I’ve overcome the initial fear, it turned out to be a quite simple and useful tool.

One feature in particular is very useful for Emacs users like me: `lein swank`. You define all dependencies in `project.clj` as usual, add a magical line to `:dev-dependencies`, then say

```
$ lein swank
```

and lo and behold, you can `M-x slime-connect` from your Emacs and have all the code at your disposal.

There is, however, an issue that you must be aware of when using `lein swank`: Leiningen uses a custom class loader — [AntClassLoader][3] to be more precise — to load the Java classes referenced by the code. Despite being a seemingly irrelevant thing — an implementation detail — this can bite you in a number of most surprising and obscure ways. Try evaluating the following code in a Leiningen REPL:

```clojure
(str (.decode
       (java.nio.charset.Charset/forName "ISO-8859-2")
       (java.nio.ByteBuffer/wrap
         (into-array Byte/TYPE (map byte [-79 -26 -22])))))
;=> "???"
```

The same code evaluated in a plain Clojure REPL will give you `"ąćę"`, which is a string represented in ISO-8859-2 by the three bytes from the above snippet.

Whence the difference? Internally, each charset is represented as a unique instance of its specific class. These are loaded lazily as needed by the `Charset/forName` method. Presumably, the system class loader is used for that, and somewhere along the way a `SecurityException` gets thrown and caught.

Note also that there are parts of Java API which use the charset lookup under the hood and are thus vulnerable to the same problem, for example `Reader` constructors taking charset names. If you use `clojure.contrib.duck-streams`, then rebinding `*default-encoding*` will not work from a Leiningen REPL. Jars and überjars produced by Leiningen should be fine, though.
