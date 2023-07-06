---
date: 2023-07-06
title: Learning to learn Rust
categories: programming rust
---

I’m enjoying a two-month sabbatical this summer. It’s been great so far! I’ve used almost half of the time to [cycle through the entire Great Britain][1] and let my body work physically and my mind rest (usually, the opposite is true). And now that I’m back, I’ve switched focus to a few personal projects that I have really wanted to work on for a while but never found time.

One of these projects is to learn Rust. Clojure has made me lazy and it’s really high time for me to flex the language-learning muscles. But while the title says “Rust,” there is nothing Rust-specific about the tip I’m about to share: it can be applied to many programming languages.

I learn best by doing, so after learning the first few chapters of [the Rust book][2], I set off to write a simple but non-trivial program: a console-based tree viewer. The idea is to have a TUI that you could feed with a set of slash-separated paths:

```
one/two
one/three/four
five/six
```

and have it render the tree visually:

```
├─ one
│  ├─ two
│  └─ three
│     └─ four
└─ five
   └─ six
```

allowing to scroll it, search it and (un)fold individual subtrees. The paths may come from the filesystem (e.g. you could pipe `find . -type f` into it), but not necessarily: they might be S3 object paths, hierarchical names of RocksDB keys (my actual use case), or represent any other tree.

Today I hit a major milestone: I [wrote a function][3], `append_path`, that, given a tree of strings and a slash-separated path, creates new nodes as needed and adds them to the tree. Needless to say, I didn’t get it right on the first attempt. I fought with the compiler and its borrow checker _a lot_.

I guess that’s a typical ordeal that a Rust newbie goes through. But along treeviewer’s code, I keep an org-mode file called `LEARN` where I jot down things that I might want to remember for the future. So after getting `append_path` right, I wanted to pause and look back at the failed attempts and the corresponding compiler errors, to try to make sense of them, armed with my new knowledge.

But… _which_ versions of the code caused _which_ errors? I had no idea! And the Emacs undo tree is really hard to dive in.

An obvious way out is to commit early and often. But this (1) requires a discipline that I don’t have at the moment, and (2) pollutes the Git history. So, instead, I automated it.

I’ve added a Makefile to my repo. Instead of `cargo run`, I will now be compiling and executing the code via `make run`. In addition to Cargo, this runs [a script][4] that:

- Commits everything that’s uncommitted yet
- Creates an annotated tag with that commit, named `build-$TIMESTAMP`, that serves as a snapshot of the code that was built
- Reverts the working tree to the state it was in (whatever was staged stays staged, whatever was unstaged remains unstaged)

This workflow change has the nice property of being unintrusive. I can hack on the code, compile, commit and rebase to my heart’s delight. But when I need to look back at the most recent compilation attempts, all I need to do is `git tag` and from there I can meditate on individual mistakes I made.

Why tags and not branches, one might ask? I guess this is a matter of personal preference. I opted for tags because I want to minimise the chance of accidentally pushing the branch. The resulting tags are technically dangling, which I don’t see as an issue: the older the build tag, the less likely I am to need it in the future, so I see myself cleaning up old builds every now and then.

When working with a language I’m proficient in, I don’t need this. But as a learning aid, I already see the idea as indispensable. Feel free to reuse it!

 [1]: https://danieljanus.substack.com/about
 [2]: https://doc.rust-lang.org/book/
 [3]: https://github.com/nathell/treeviewer/commit/fb1332aa5bd0f695604522492ccd893dac28066a
 [4]: https://github.com/nathell/treeviewer/blob/main/scripts/record.sh
