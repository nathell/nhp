---
date: 2020-05-02
title: Clojure as a dependency
categories: Lisp Clojure programming
---

I have a shameful confession to make: I have long neglected an open-source library that I maintain, [clj-tagsoup][1].

This would have been less of an issue, but this is my second-most-starred project on GitHub. Granted, I don’t feel a need for it anymore, but apparently people do. I wish I had spent some time reviewing and merging the incoming PRs.

Anyway, I’ve recently been prompted to revive it, and I’m preparing a new release. While on it, I’ve been updating dependencies to their latest versions, and upon seeing a dependency on `[org.clojure/clojure "1.2.0"]` in `project.clj` (yes, it’s been neglected for that long), I started wondering: which Clojure to depend on? Actually, should Clojure itself be a dependency at all?

I’ve googled around for best practices, but with no conclusive answer. So I set out to do some research.

**TLDR:** with Leiningen, add it with `:scope "provided"`; with cli-tools, you don’t have to, unless you want to be explicit.

## Is it possible for a Clojure project to declare no dependency on Clojure at all?

Quite possible, as it turns out. But the details depend on the build tool.

Obviously, this only makes sense for libraries. Or, more broadly, for projects that are not meant to be used standalone, but rather included in other projects (which will have a Clojure dependency of their own).

### Leiningen

If you try to create a Leiningen project that has no dependencies:

```clojure
(defproject foo "0.1.0"
  :dependencies [])
```

then Leiningen (as of version 2.9.3, but I’d guess older versions behave similarly) won’t allow you to launch a REPL:

```
$ lein repl
Error: Could not find or load main class clojure.main
Caused by: java.lang.ClassNotFoundException: clojure.main
Subprocess failed (exit code: 1)
```

But all is not lost: `lein jar` works just fine (as long as you don’t AOT-compile any namespaces), as does `lein install`. The resulting library will happily function as a dependency of other projects.

The upside of depending on no particular Clojure version is that you don’t impose it on your consumers. If a library depends on Clojure 1.9.0, but a project that uses it depends on Clojure 1.10.1, then Leiningen will fetch 1.9.0’s `pom.xml` (it’s smart enough to figure out that the jar itself won’t be needed, as the conflict will always be resolved in favour of the direct dependency), and `lein deps :tree` will report “possibly confusing dependencies”.

It’s not very useful to have a library that you can’t launch a REPL against, though. So what some people do is declare a dependency on Clojure not in the main `:dependencies`, but in a profile.

```clojure
(defproject foo "0.1.0"
  :dependencies []
  :profiles {:dev {:dependencies [[org.clojure/clojure "1.10.1"]]}})
```

This avoids conflicts and brings back the possibility to launch a REPL. Sometimes, people create multiple profiles for different Clojure versions; [Leiningen’s documentation][2] mentions this possibility.

Unfortunately, with this approach it’s still not possible to AOT-compile things or create uberjars with Leiningen. (Putting Clojure in the `:provided` profile causes building the uberjar to succeed, but the resulting `-standalone` jar doesn’t actually contain Clojure).

Another option is to add Clojure to the main `:dependencies`, but with `:scope "provided"`. Per the [Maven documentation][3], this means:

> This is much like `compile`, but indicates you expect the JDK or a container to provide the dependency at runtime. For example, when building a web application for the Java Enterprise Edition, you would set the dependency on the Servlet API and related Java EE APIs to scope `provided` because the web container provides those classes. This scope is only available on the compilation and test classpath, and is not transitive.

The key are the last words: “not transitive.” If project A depends on a library B that declares a “provided” dependency C, then C won’t be automatically put in A’s dependencies, and A is expected to explicitly declare its own C.

This means that it’s adequate for both libraries and standalone projects when it comes to declaring a Clojure dependency. It doesn’t break anything, doesn’t cause any ephemeral conflicts, and can be combined with the profiles approach when multiple configurations are called for.

 [1]: https://github.com/nathell/clj-tagsoup
 [2]: https://github.com/technomancy/leiningen/blob/master/doc/PROFILES.md
 [3]: http://maven.apache.org/guides/introduction/introduction-to-dependency-mechanism.html

### cli-tools

cli-tools will accept a `deps.edn` as simple as `{}`. Even passing `-Srepro` to `clojure` or `clj` (which excludes the Clojure dependency that you probably have in your `~/.clojure/deps.edn`) doesn’t break anything: cli-tools will just use 1.10.1 (at least as of version 1.10.1.536).

With cli-tools, as a library author you probably don’t have to declare a Clojure dependency at all. But things are less uniform in this land than they are in Leiningen (for example, there are quite a few uberjarrers to choose from), so it’s reasonable to check with your tooling first.

### Boot

I’m no longer a Boot user, so I can’t tell. But from what I know, it uses Aether just like Leiningen and Maven do, so I’d wager a guess the same caveats apply as for Leiningen. Haven’t checked, though.

## So what do the existing projects do?

I figured it would be a fun piece of research to examine how the popular projects depend (or don’t depend) on Clojure. I queried GitHub’s API for the 1000 most starred Clojure projects, fetched and parsed their `project.clj`s and/or `deps.edn`s, and tallied things up.

I’ll write a separate “making of” post, because it turned out to be an even more fun weekend project than I had anticipated. But for now, let me share the conclusions.

I ended up with 968 project definition files that I was able to successfully parse: 140 `deps.edn`s and 828 `project.clj`s. Here’s a breakdown of Clojure version declared as a “main” dependency (i.e., not in a profile or alias):

<img src="/img/blog/clojure-versions.png">

N/A means that there’s no dependency on Clojure declared, and “other” is an umbrella for the zoo of alphas, betas and snapshots.

As expected, not depending on Clojure is comparatively more popular in the cli-tools land: almost half (48.6%) of cli-tools projects don’t declare a Clojure dependency, versus 21.5% (174 projects) for Leiningen.

That Leiningen number still seemed quite high to me, so I dug a little deeper. Out of those 174 projects, 100 have Clojure somewhere in their `:profiles`. The remaining 74 are somewhat of outliers:

- some, like Ring or Pedestal, are umbrella projects composed of sub-projects (with the `lein-sub` plugin) that have actual dependencies themselves;
- some, like Klipse or Reagent, are essentially ClojureScript-only;
- some, like Overtone, use the `lein-tools-deps` plugin to store their dependencies in `deps.edn` while using Leiningen for other tasks.

Finally, the popularity of `:scope "provided"` is much lower. Only 68 Leiningen projects specify it (8.9% of those that declare any dependencies), and only two `deps.edn` files do so (re-frame and fulcro – note that re-frame actually has both a `project.clj` and a `deps.edn`).
