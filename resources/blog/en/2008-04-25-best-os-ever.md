---
date: 2008-04-25
title: Best OS ever
categories: Ubuntu admin
---

If you are reading this on a box that does not have an impressive amount of RAM (say, 512 MB or less) and is running a fairly recent Linux, then for goodness sake, drop everything you are doing right now and follow the instructions in this entry. I’m going to show you how to make your system use the memory in a more efficient way, _yielding an effect almost equivalent to increasing its amount — with no expenses whatsoever!_ Sounds good? Read on.

You see, there’s this Linux kernel module for kernels 2.6.17 and up (that’s what the phrase fairly recent in the previous paragraph macroexpands to), called [Compcache][1]. It works by slicing out a contiguous chunk of your RAM (25% by default, but it’s settable, of course) and setting it up as a swap space with highmost priority. The trick is that pages that are swapped out to this area are compressed using the [LZO][2] algorithm, which provides very fast compression/decompression while maintaining a decent compression ratio. In this way, more unused pages can fit in memory, and less of them are swapped out to disk, which can considerably cut down disk swap usage. I’ve enabled it in my system and it doesn’t seem to cause any problems, while providing a visible efficiency boost. Here’s how I did it on a freshly-installed [Ubuntu Hardy][3]:

- I installed the Ubuntu package `build-essential`, then downloaded Compcache from its site, extracted it, entered its directory and compiled it by saying make. So far, so easy.
- Unfortunately, one cannot say `make install` — creating a flexible cross-distro `install` target is admittedly hard. So I installed it by hand, ensuring that my system enables it automatically on boot-up.
- I created a directory `/lib/modules/2.6.24-16-generic/ubuntu/compcache/` and copied the four kernel modules (`compcache.ko`, `lzo1x_compress.ko`, `lzo1x_decompress.ko`, and `tlsf.ko`) created by the compilation to that directory.
- Next, I ran `depmod -a` to make the modules loadable by `modprobe`.
- I edited the file `/etc/modules` and added a line at the end, containing the single word `compcache`.
- I copied the shell scripts `use_compcache.sh` and `unuse_compcache.sh` that come with compcache to `/usr/local/bin`.
- I created an executable script `/etc/init.d/compcache` with the following contents:
```bash
#!/bin/sh
case "$1" in
  start)
    /usr/local/bin/use_compcache.sh ;;
  stop)
    /usr/local/bin/unuse_compcache.sh ;;
esac
```
- The last step was to create a symlink `/etc/rc2.d/S02compcache` pointing to that script.

I then rebooted the system and verified that the new swapspace is in use:

```bash
nathell@chamsin:~$ cat /proc/swaps
Filename        Type        Size    Used    Priority
/dev/sdb2       partition   996020  0       -1
/dev/ramzswap0  partition   128896  111396  100
```

With the final release of Hardy installed on my main box and compcache optimizing its memory usage, I do not hesitate to call this combo the best OS I have ever had installed.

And no, I don’t own a Mac. :-/

 [1]: http://code.google.com/p/compcache
 [2]: http://www.oberhumer.com/opensource/lzo/
 [3]: http://www.ubuntu.com/products/whatisubuntu/804features/
