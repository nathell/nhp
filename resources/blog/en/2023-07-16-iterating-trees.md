---
date: 2023-07-20
title: A visual tree iterator in Rust
categories: programming rust
---

My [adventure with learning Rust][1] continues. As a quick recap from the previous post, I’m writing a [tree viewer][2]. I have now completed another major milestone, which is to rewrite the tree-printing function to use an iterator. (Rationale: it makes the code more reusable – I can, for instance, easily implement a tree-drawing view for [Cursive][3] with it.)

 [1]: /2023/07/06/learning-to-learn-rust/
 [2]: https://github.com/nathell/treeviewer
 [3]: https://github.com/gyscos/cursive

And, as usual, I’ve fallen into many traps before arriving at a working version. In this post, I’ll reflect on the mistakes I’ve made.

## The problem

Let’s start with establishing the problem. Given a `Tree` struct defined as:

```rust
pub struct Tree<T> {
    value: T,
    children: Vec<Tree<T>>,
}
```

I want it to have a `lines()` method returning an iterator, so that I can implement `print_tree` as:

```rust
fn print_tree<T: Display>(t: &Tree<T>) {
    for line in t.lines() {
        println!("{}", line);
    }
}
```

and have the output identical to the previous version.

## The algorithm

Before we dive into the iterator sea, let’s have a look at the algorithm. Imagine that we’re printing the tree (in sexp-notation) `(root (one (two) (three (four))) (five (six)))`. This is its dissected visual representation:

<img src="/img/blog/tree-anatomy.png" alt="Anatomy of a tree">

Each line consists of three concatenated elements, which I call “parent prefix”, “immediate prefix”, and “node value”. The immediate prefix is always (except for the root node) `"└─ "` or `"├─ "`, depending on whether the node in question is the last child of its parent or not. The parent prefix has variable length that depends on the node’s depth, and has the following properties:

- For any node, all its subnodes’ parent prefixes start with its parent prefix.
- For any node, the parent prefixes of its direct children are obtained by appending `"   "` or `"│  "` to its own parent prefix, again depending on whether the node is its parent’s last child or not.

This gives rise to the following algorithm that calls itself recursively:

```rust
fn print_tree<T>(t: &Tree<T>,
                 parent_prefix: &str,
                 immediate_prefix: &str,
                 parent_suffix: &str)
    where T: Display
{
    // print the line for node t
    println!("{0}{1}{2}", parent_prefix, immediate_prefix, t.value);

    // print all children of t recursively
    let mut it = t.children.iter().peekable();
    let child_prefix = format!("{0}{1}", parent_prefix, parent_suffix);

    while let Some(child) = it.next() {
        match it.peek() {
            None    => print_tree(child, &child_prefix, "└─ ", "   "),
            Some(_) => print_tree(child, &child_prefix, "├─ ", "│  "),
        }
    }
}
```

The three extra string arguments start out as empty strings and become populated as the algorithm descends into the tree. The implementation uses a [peekable][4] iterator over the `children` vector to construct the prefixes appropriately.

 [4]: https://doc.rust-lang.org/stable/std/iter/struct.Peekable.html

## Building an iterator, take 1

So the printing implementation is recursive. How do we write a recursive iterator in Rust? Is it even possible? I initially thought I would have to replace the recursion with an explicit stack stored in the iterator’s mutable state, started to write some code, and promptly got lost.

I then searched for the state-of-the-art on iterating through trees, and found [this post][5] by Amos Wenger. You might want to read it first before continuing; my final implementation ended up being an adaptation of one of the techniques described there.

 [5]: https://fasterthanli.me/articles/recursive-iterators-rust

My definition of tree is slightly different than Amos’s (mine has only one value in a node), but it’s easy enough to adapt his final solution to iterate over its values:

```rust
impl<T> Tree<T> where T: Display {
    pub fn lines<'a>(&'a self) -> Box<dyn Iterator<Item = String> + 'a> {
        let child_iter = self.children.iter().map(|n| n.lines()).flatten();

        Box::new(
            once(self.value.to_string()).chain(child_iter)
        )
    }
}
```

(Note the `dyn` keyword; Rust started requiring it in this context sometime after Amos’s article was published.)

Clever! This sidesteps the issue of writing a custom iterator altogether, by chaining some standard ones, wrapping them in a box and sprinkling some lifetime annotation magic powder to appease the borrow checker. We also make it explicit that the iterator is returning strings, no matter what the type of tree nodes is.

_But…_ while it compiles and produces a sequence of strings, they don’t reflect the structure of the tree: there’s no pretty prefixing going on.

Let’s try to fix that. Clearly, the iterator-returning function will now need to take three additional arguments, just like `print_tree` – the first one will now be a `String` because we’ll be building it at runtime, and the other two are string literals so can just be `&'static str`s. Let’s try:

```rust
// changing the name because we now accept extra params
// I want the original lines() to keep its signature
pub fn prefixed_lines<'a>(&'a self,
                          parent_prefix: String,
                          immediate_prefix: &'static str,
                          parent_suffix: &'static str)
                         -> Box<dyn Iterator<Item = String> + 'a>
{
    let value = format!("{0}{1}{2}", parent_prefix, immediate_prefix, self.value);
    let mut peekable = self.children.iter().peekable();
    let child_iter = peekable
        .map(|n| {
            let child_prefix = format!("{0}{1}", parent_prefix, parent_suffix);
            let last = !peekable.peek().is_some();
            let immediate_prefix = if last { "└─ " } else { "├─ " };
            let parent_suffix = if last { "   " } else { "│  " };
            n.prefixed_lines(child_prefix, immediate_prefix, parent_suffix)
        })
        .flatten();

    Box::new(
        once(value).chain(child_iter)
    )
}
```

And, sure enough, it doesn’t compile. One of the things that Rust complains about is:

```
error[E0373]: closure may outlive the current function,
    but it borrows `peekable`, which is owned by the current function
  --> src/main.rs:55:18
   |
55 |     .map(|n| {
   |          ^^^ may outlive borrowed value `peekable`
56 |         let child_prefix = format!("{0}{1}"...
57 |         let last = !peekable.peek().is_some();
   |                     -------- `peekable` is borrowed here
   |
note: closure is returned here
  --> src/main.rs:64:9
   |
64 | Box::new(once(value).chain(child_iter))
   | ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
help: to force the closure to take ownership of `peekable`
      (and any other referenced variables), use the `move` keyword
   |
55 |     .map(move |n| {
   |          ++++
```

So trying to borrow the iterator from within the closure passed to `map()` is non-kosher. I’m not sure where the “may outlive the current function” comes from, but I think this is because [the iterator returned by `map` is lazy][6], and so the closure needs to be able to live for at least as long as the resulting iterator does. The suggestion of using `move` doesn’t work, because it then invalidates the `map` call. (Rust complained about borrowing `parent_prefix` and `parent_suffix` as well, and `move` does work for those.)

 [6]: https://doc.rust-lang.org/std/iter/trait.Iterator.html#method.map

## Taking a step back

I was not able to find a way out of this conundrum. But after re-reading Amos’s post, I’ve decided to revisit his “bad” approach, with a custom iterator (which I now think is actually not bad at all). It made all the more sense to me when I considered future extensibility: eventually I want to be able to render certain subtrees collapsed, and I want the iterator to know about that.

It took me a while to understand how that [custom iterator][7] works. It doesn’t have an explicit stack and doesn’t try to “de-recursivize” the process! Instead, it holds two sub-iterators, one initially iterating over the node values (`viter`) and the other over children (`citer`). The `next()` method just tries `viter` first; if it returns nothing, then a next subtree is picked from `citer`, and `viter` (by now already consumed) _is replaced by another instance of the same iterator, but for that subtree_.

 [7]: https://play.rust-lang.org/?version=stable&mode=debug&edition=2018&gist=c2cf6a965c3637553edd95eecc1993cd

Meditate on this for a while. There’s a lot going on here.

 - `viter` starts out as an iterator over a vector (a `std::slice::Iter`), and then gets replaced by a tree iterator (Amos’s `NodeIter`).
 - This is possible because it’s declared as a `Box&lt;Iterator&lt;Item = &'a i32> + 'a>`. TIL: in Rust, you can’t use a trait directly as a type for a struct field (because there’s no telling what its size will be), but you _can_ put it into a `Box` (or, I guess, `Rc` or `Arc`). Polymorphism, baby!
 - Recursion is achieved by having `NodeIter` contain a member that, at times, is itself another `NodeIter`; whereas the correct behaviour is obtained by having those `NodeIters` instantiated at the right moment.

Whoa. Now _that’s_ clever. I probably wouldn’t have thought about this. It’s good to be standing on the shoulders of giants. Thanks, Amos.

Anyway, let’s adapt it to our use-case and add the prefixes to the iterator’s state:

```rust
pub struct TreeIterator<'a, T> {
    parent_prefix: String,
    immediate_prefix: &'static str,
    parent_suffix: &'static str,
    viter: Box<dyn Iterator<Item = String> + 'a>,
    citer: Box<dyn Iterator<Item = &'a Tree<T>> + 'a>,
}
```

And our iterator implementation follows Amos’s, except that we handle the prefixes and initialize `viter` with a [`Once`][8] iterator:

 [8]: https://doc.rust-lang.org/std/iter/struct.Once.html

```rust
impl<T> Tree<T> where T: Display {
    pub fn prefixed_lines<'a>(&'a self,
                      parent_prefix: String,
                      immediate_prefix: &'static str,
                      parent_suffix: &'static str)
                     -> TreeIterator<'a, T>
    {
        TreeIterator {
            parent_prefix: parent_prefix,
            immediate_prefix: immediate_prefix,
            parent_suffix: parent_suffix,
            viter: Box::new(once(format!("{}", &self.value))),
            citer: Box::new(self.children.iter().peekable()),
        }
    }
}

impl<'a, T> Iterator for TreeIterator<'a, T> where T: Display {
    type Item = String;

    fn next(&mut self) -> Option<Self::Item> {
        if let Some(val) = self.viter.next() {
            Some(format!("{0}{1}{2}", self.parent_prefix, self.immediate_prefix, val))
        } else if let Some(child) = self.citer.next() {
            let last = !self.citer.peek().is_some();
            let immediate_prefix = if last { "└─ " } else { "├─ " };
            let parent_suffix = if last { "   " } else { "│  " };
            let subprefix = format!("{0}{1}", self.parent_prefix, self.parent_suffix);
            self.viter = Box::new(child.prefixed_lines(subprefix, immediate_prefix, parent_suffix));
            self.next()
        } else {
            None
        }
    }
}
```

Looks sensible, right? Except (you guessed it!) it doesn’t compile:

```rust
error[E0599]: no method named `peek` found for struct
    `Box<(dyn Iterator<Item = &'a Tree<T>> + 'a)>` in the current scope
  --> src/main.rs:38:36
   |
38 |     let last = !self.citer.peek().is_some();
   |                            ^^^^ help: there is a method with a
   |                                 similar name: `peekable`
```

Ah, right. We’ve forgotten to tell Rust that `citer` contains a `Peekable`. Let’s fix that:

```rust
pub struct TreeIterator<'a, T> {
    // … other fields as before
    citer: Box<Peekable<dyn Iterator<Item = &'a Tree<T>> + 'a>>,
}
```

Nope, that doesn’t compile either:

```rust
error[E0277]: the size for values of type `(dyn Iterator<Item = &'a Tree<T>> + 'a)`
    cannot be known at compilation time
  --> src/main.rs:16:12
   |
16 |     citer: Box<Peekable<dyn Iterator<Item = &'a Tree<T>> + 'a>>,
   |            ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   |            doesn't have a size known at compile-time
   |
   = help: the trait `Sized` is not implemented for
           `(dyn Iterator<Item = &'a Tree<T>> + 'a)`
note: required by a bound in `Peekable`
```

Bummer. We can put a trait of unknown size in a `Box`, but we can’t put a `Peekable` in between! `Peekable` needs to know the size of its contents at compile time. Trying to convince it by sprinkling `+ Sized` in various places doesn’t work.

Fortunately, we know the _actual_ type of `citer`. It’s an iterator over `Vec&lt;Tree&lt;T>>`, so it’s a `std::slice::Iter&lt;Tree&lt;T>>`. Let’s put it in the definition of `TreeIterator`:

```rust
use std::slice::Iter;

pub struct TreeIterator<'a, T> {
    // … other fields as before
    citer: Box<Peekable<Iter<'a, Tree<T>>>>,
}
```

And it compiles!

## Removing the root

Here’s what happens when you try to run treeviewer with this implementation on a very simple tree:

```bash
$ echo -e 'one\ntwo' | ./target/debug/treeviewer

├─ one
└─ two
```

Seems good, but that empty line is worrying. That’s because treeviewer takes slash-separated paths as input, and because the paths can begin with anything, it puts everything under a pre-existing root node with an empty `value`. We don’t want the output to contain that root node.

Simple, right? We just need to initialize `viter` with an empty iterator if one of the prefixes is also empty:

```rust
pub fn prefixed_lines<'a>(&'a self,
                          parent_prefix: String,
                          immediate_prefix: &'static str,
                          parent_suffix: &'static str)
                         -> TreeIterator<'a, T>
{
    TreeIterator {
        // … other fields as before
        viter: Box::new(if immediate_prefix.is_empty() {
                           empty()
                        } else {
                           once(format!("{}", &self.value))
                        }),
    }
}
```

And (this is becoming obvious by now) we’re rewarded by yet another interesting error message:

```rust
error[E0308]: `if` and `else` have incompatible types
  --> src/main.rs:49:32
   |
46 |   viter: Box::new(if immediate_prefix.is_empty() {
   |  _________________-
47 | |                    empty()
   | |                    ------- expected because of this
48 | |                 } else {
49 | |                    once(format!("{}", &self.value))
   | |                    ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
   | |                      expected `Empty<_>`, found `Once<String>`
50 | |                 }),
   | |_________________- `if` and `else` have incompatible types
   |
   = note: expected struct `std::iter::Empty<_>`
              found struct `std::iter::Once<String>`
```

Ahhh. Even though both branches of the `if` expression have types that meet the trait requirement (`Iterator<Item = String>`), these are _different types_. Apparently, `if` insists on both branches being the same type.

What we can do is lift the `if` upwards:

```rust
pub fn prefixed_lines<'a>(&'a self,
                          parent_prefix: String,
                          immediate_prefix: &'static str,
                          parent_suffix: &'static str)
                         -> TreeIterator<'a, T>
{
    if immediate_prefix.is_empty() {
        TreeIterator {
            // … other fields as before
            viter: Box::new(empty()),
        }
    } else {
        TreeIterator {
            // … other fields as before, repeated
            viter: Box::new(once(format!("{}", &self.value))),
        }
    }
}
```

Yuck. We needed to duplicate most of the instantiation details of `TreeIterator`. But at least it compiles and works – the root is gone!

```bash
$ echo -e 'one\ntwo' | ./target/debug/treeviewer
├─ one
└─ two
```

## Fixing a bug

Or does it? Let’s try the original tree from our illustration:

```bash
$ echo -e 'one/two\none/three/four\nfive/six' | ./target/debug/treeviewer
├─ one
├─ │  ├─ two
├─ │  └─ three
├─ │  └─ │     └─ four
└─ five
└─    └─ six
```

Uh oh. It’s totally garbled. Time to go back to the drawing board.

It took me quite a few `println!()` debugging statements to figure out what was going on. Remember, the `TreeIterator` for the whole tree will contain a nested `TreeIterator` in its `viter` field, which in turn may contain another nested `TreeIterator`, and so on. Each of these nested iterators eventually passes its value to the “parent” iterator… decorating it with prefixes, again and again!

To fix this, we need to differentiate between two cases:

1. We’re producing the value for the node we’re holding (that’s when we need the prefixes);
2. We’re propagating up the value returned by `viter` that holds a nested `TreeIterator` (in this case we need to return it unchanged).

We’ll add two more fields to `TreeIterator`: a boolean indicating whether we’ve already `emitted` the value at the node in question, and a reference to that `value` itself.

```rust
pub struct TreeIterator<'a, T> {
    // … other fields as before
    emitted: bool,
    value: &'a T,
}
```

And we initialize them as follows:

```rust
pub fn prefixed_lines<'a>(&'a self,
                          parent_prefix: String,
                          immediate_prefix: &'static str,
                          parent_suffix: &'static str)
                         -> TreeIterator<'a, T>
{
    TreeIterator {
        emitted: immediate_prefix.is_empty(),
        value: &self.value,
        viter: Box::new(empty()),
        // … other fields as before
    }
}
```

Note that the logic of skipping emitting the root has been moved to the initialization of `emitted`. This lets us kill the duplication! We now initialize `viter` to `empty()` – it no longer matters; this initial value will be unused and eventually replaced by child `TreeIterator`s.

Finally, we need to amend the implementation of `next()`:

```rust
fn next(&mut self) -> Option<Self::Item> {
    if !self.emitted {
        self.emitted = true;
        // decorate value with prefixes
        Some(format!("{0}{1}{2}", self.parent_prefix, self.immediate_prefix, self.value))
    } else if let Some(val) = self.viter.next() {
        Some(val) // propagate unchanged
    } else if let Some(child) = self.citer.next() {
        // … this part doesn’t change
    } else {
        None
    }
}
```

And _this_ version, finally, compiles and works as expected:

```bash
$ echo -e 'one/two\none/three/four\nfive/six' | ./target/debug/treeviewer
├─ one
│  ├─ two
│  └─ three
│     └─ four
└─ five
   └─ six
```

## Takeaways

There are quite a few things I learned about Rust in the process, and then there are meta-learnings. Let’s recap the Rust-specific ones first.

- You can’t put a trait in a struct directly, but you can put a `Box` of traits.
- But not a `Box` of `Foo` of traits, where `Foo` expect its parameter to be `Sized`.
- If you’re `map()`ping a closure over an iterator, you can’t access that iterator itself from within the closure.
- Closures by default borrow stuff that they close over, but you can move that stuff to the closure instead with the `move` keyword. If I understand correctly, it’s an all-or-nothing move; no mix and match.
- In an `if` expression, all branch expressions must be of the same type; conforming to the same trait is not enough.

And now the general ones.

First off, Rust is _hard_. (The least wonder in the world.) Most of the traps I’ve fallen into are accidental complexity, not inherent in the simple problem. I guess that it’s really a matter of the initial steepness of Rust’s learning curve, and that things become easier once you’re past the initial hurdles – you train your instincts to avoid these tarpits and keep the compiler happy.

I’m still very much a newcomer to Rust, so I’m pretty sure I ended up taking a suboptimal approach. A seasoned Rustacean would probably write this code in an altogether different way. If you have suggestions how to improve my code, or how to attack the problem from different angles, tell me!

As an experiment in learning, I’ve decided to reflect on my mistakes more frequently. I elaborate on it in my [previous post][9], which also discusses changes I’ve made to my workflow to make learning easier.

 [9]: /2023/07/06/learning-to-learn-rust/

Writing the present post showed me how much time it takes. It took me just over an hour to fall into all the traps described in this post and find a way out. A few hours, if you count reading Amos’s post and contemplating the problem. In contrast, this write-up took about two days, plus some [yak shaving][10] it led me to. Part of the reason is that the _actual_ road that I went through was much more bumpy than described here. While writing this, I had to go through no fewer than fifty-six compilation attempts. Here are some of them, with one-line descriptions and a tick or cross to indicate whether the compilation attempt was successful:

 [10]: https://mastodon.social/@nathell/110725780205595986

<img src="/img/blog/rust-compilation-attempts.png" alt="Some compilation attempts">

Yet I think it’s worth it. Some of the errors I’ve fixed groping in the dark, kind of randomly: I have now revisited them and I feel I have a much more solid understanding of what’s going on.

And finally: if you’re into Rust, Amos’s blog ([fasterthanli.me][10]) is an excellent resource. Go sponsor him on GitHub if these articles are of value to you.

 [11]: https://fasterthanli.me/
