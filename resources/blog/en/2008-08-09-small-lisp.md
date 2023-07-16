---
date: 2008-08-09
title: "Who said Common Lisp programs cannot be small?"
categories: Lisp programming
---

So, how much disk space does your average CL image eat up? A hundred megs? Fifty? Twenty? Five, perhaps, if you’re using LispWorks with a tree-shaker? Well then, how about this?

```
[nathell@chamsin salza2-2.0.4]$ ./cl-gzip closures.lisp test.gz
[nathell@chamsin salza2-2.0.4]$ gunzip test
[nathell@chamsin salza2-2.0.4]$ diff closures.lisp test
[nathell@chamsin salza2-2.0.4]$ ls -l cl-gzip
-rwxr-xr-x 1 nathell nathell 386356 2008-08-09 11:08 cl-gzip
```

That’s right. A standalone executable of a mini-gzip, written in Common Lisp, taking up _under 400K!_ And it only depends on glibc and GMP, which are available by default on pretty much every Linux installation. (This is on a 32-bit x86 machine, by the way).

I used the most recent version of [ECL][1] for compiling this tiny example. The key to the size was configuring ECL with `--disable-shared --enable-static CFLAGS="-Os -ffunction-sections -fdata-sections" LDFLAGS="-Wl,-gc-sections"`. This essentially gives you a poor man’s tree shaker for free at a linker level. And ECL in itself produces comparatively tiny code.

I built this example from [Salza2][2]’s source by loading the following code snippet:

```lisp
(defvar salza
  '("package" "reset" "specials"
    "types" "checksum" "adler32" "crc32" "chains"
    "bitstream" "matches" "compress" "huffman"
    "closures" "compressor" "utilities" "zlib"
    "gzip" "user"))

(defvar salza2
  (mapcar (lambda (x) (format nil "~A.lisp" x))
          salza))

(defvar salza3
  (mapcar (lambda (x) (format nil "~A.o" x))
          salza))

(defun build-cl-gzip ()
  (dolist (x salza2)
          (load x)
          (compile-file x :system-p t))
  (c:build-program
   "cl-gzip"
   :lisp-files salza3
   :epilogue-code
     '(progn
       (in-package :salza2)
       (gzip-file (second (si::command-args))
                  (third (si::command-args))))))

(build-cl-gzip)
```

(Sadly enough, there’s no ASDF in here. I have yet to figure out how to leverage ASDF to build small binaries in this constrained environment.)

This gave me a standalone executable 1.2 meg in size. I then proceeded to compress it with [UPX][3] (with arguments `--best --crp-ms=999999`) and got the final result. How cool is that?

I am actively looking for a new job. If you happen to like my writings and think I might be just the right man for the team you’re building up, please feel free to consult my [résumé][4] or pass it on.

_Update 2010-Jan-17_: the above paragraph is no longer valid.

 [1]: http://ecls.sourceforge.net/
 [2]: http://www.xach.com/lisp/salza2
 [3]: http://upx.sourceforge.net/
 [4]: http://bach.ipipan.waw.pl/~nathell/cv-en.pdf
