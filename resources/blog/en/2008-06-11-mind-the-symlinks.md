---
date: 2008-06-11
title: "Today’s lesson: Mind the symlinks"
categories: Unix
---

Probably every day I keep learning new things, without even realizing it most of the time. The vast majority of them are minor or even tiny tidbits of knowledge; but even these might be worth noting down from time to time, especially when they are tiny pitfalls I’d fallen into and spent a couple of minutes getting out. By sharing them, I might hopefully prevent someone else for slipping and falling in.

So here’s a simple Unix question: If you enter a subdirectory of the current directory and back to `..`, where will you end up? The most obvious answer is, of course, “in the original directory”, and is mostly correct. But is it always? Let’s see.

```nohighlight
nathell@breeze:~$ pwd
/home/nathell
nathell@breeze:~$ cd foobar
nathell@breeze:~/foobar$ cd ..
nathell@breeze:~$ pwd
/home/nathell
```

So the hypothesis seems to be right. But let’s try doing this in Python, just for the heck of it:

```nohighlight
nathell@breeze:~$ python
Python 2.5.2 (r252:60911, Apr 21 2008, 11:12:42)
[GCC 4.2.3 (Ubuntu 4.2.3-2ubuntu7)] on linux2
Type "help", "copyright", "credits" or "license" for more information.
>>> import os
>>> print os.getcwd()
/home/nathell
>>> os.chdir("foobar")
>>> os.chdir("..")
>>> print os.getcwd()
/var
```

Whoa, hang on! What’s that `/var` doing there? Of course the one thing I didn’t tell you is that `foobar` is not really a directory, but rather a symlink pointing to one (`/var/log` in this case).

The corollary is that the shell builtin `cd` is _not the same_ as Unix `chdir()` (it is easily checked that both Perl and C exhibit the same behaviour). In fact, the shell builtin has an oft-forgotten command-line switch, `-P`, which causes it to follow physical instead of logical path structure.

On a closing note: I have somewhat neglected the blog throughout the previous month, but I hope to revive it soon. It is not unlikely that such irregularities will recur.
