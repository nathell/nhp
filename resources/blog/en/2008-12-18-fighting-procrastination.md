---
date: 2008-12-18
title: anti-procrastination.el
categories: Emacs procrastination programming
---

Fighting procrastination has been my major concern these days. I’ve devised a number of experimental tools to help me with that. One of them is called [snafu][1] and can generate reports of your activity throughout the whole day of work. It’s in a preliminary state, but works (at least since I’ve found and fixed a long-standing bug in it which would cause it to barf every now and then), and I already have a number of ideas for its further expansion.

Reports alone, however, do not quite muster enough motivation for work. I’m doing most of my editing/programming work in Emacs, so yesterday I grabbed the Emacs Lisp manual and came up with a couple of extra lines at the end of my `.emacs`.

```lisp
;;; Written by Daniel Janus, 2008/12/18.
;;; This snippet is placed into the public domain.  Feel free
;;; to use it in any way you wish.  I am not responsible for
;;; any damage resulting from its usage.

(defvar store-last-modification-time t)
(defvar last-modification-time nil)
(defun mark-last-modification-time (beg end len)
  (let ((b1 (substring (buffer-name (current-buffer)) 0 1)))
    (when (and store-last-modification-time
               (not (string= b1 " "))
               (not (string= b1 "*")))
      (setq last-modification-time (current-time)))))
(add-hook 'after-change-functions 'mark-last-modification-time)
(defun write-lmt ()
  (setq store-last-modification-time nil)
  (when last-modification-time
    (with-temp-file "/tmp/emacs-lmt"
      (multiple-value-bind (a b c) last-modification-time
        (princ a (current-buffer))
        (terpri (current-buffer))
        (princ b (current-buffer)))))
  (setq store-last-modification-time t))
(run-at-time nil 1 'write-lmt)
```

Every second (to change that to every 10 seconds, change the `1` to `10` in the last line) it creates a file named `/tmp/emacs-lmt` which contains the time of last modification of any non-system buffer.

That’s all there is to it, at least on the Emacs side. The other part is a simple shell script, which uses [MPlayer][2] to display a nag-screen for five seconds, and then give me some time to start doing anything useful before nagging me again:

```bash
#!/bin/bash
TIMEOUT=300
while true; do
   cat /tmp/emacs-lmt | (
      read a; read b;
      c="`date +%s`";
      let x=c-65536*a-b;
      if test $x -gt $TIMEOUT;
          then mplayer -fs $HOME/p.avi;
               sleep 15;
      fi)
   sleep 1
done
```

The nag-screen in my case is an animation which I’ve created using MEncoder from a single frame which looks [like this][3]. Beware the expletives! (This is one of the few cases I find their usage justified, as the strong message bites the conscience more strongly.)

I’ve only been testing this setup for one day, but so far it’s working flawlessly: I got more done yesterday than for the two previous days combined, and that’s excluding the hour or so that took me to write these snippets.

If anyone else happens to give it a try, I’d love to hear any comments.

 [1]: http://bach.ipipan.waw.pl/~nathell/projects/snafu.php
 [2]: http://www.mplayerhq.hu/
 [3]: http://bach.ipipan.waw.pl/~nathell/procrastination.png
