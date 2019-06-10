---
title: Ubuntu post-installation tricks
date: 2008-04-07
categories: Ubuntu admin
---

Yesterday, my level of frustration with my old operating system at work exceeded a critical point, and I installed a fresh daily build of the not-yet-released [Ubuntu 8.04][1] in place of it. Then, in addition to usual post-installation chores like setting up mail, hardware, etc., I performed a couple of steps to make the system more pleasurable to use. Here’s what I did, just in case someone finds this useful.

1. First, I tweaked the font rendering. This was one area that has long been a PITA for Linux users (at least for me, since 2000 or so), but as far as Ubuntu is concerned, they introduced a change to Freetype somewhere along the way between Feisty and Gutsy which, when set up properly, makes the font rendering on LCD displays far superior for me to that of, say, Windows XP, in particular at small font sizes. The way to enable it is to enable sub-pixel rendering, and set the hinting level to “slight.” This results in a rendering very close to what the author of [Texts Rasterization Exposures][2] managed to achieve.

2. I installed the package `msttcorefonts` to get Microsoft’s free-as-in-beer set of core TrueType fonts, including Times New Roman, Arial, Georgia, etc. There are very many sites out there on the Web that were designed with these fonts in mind, and this is one of the few areas Microsoft doesn’t completely suck at.

3. Next I enabled bitmap fonts. The way to do this is to become root, cd to `/etc/fonts/conf.d`, remove the symlink named `70-no-bitmaps.conf`, and make a symlink pointing to `/etc/fonts/conf.avail/70-yes-bitmaps.conf` instead. This would come in handy in the next step.

4. Which was installing my favourite console font. Unfortunately, it doesn’t come preinstalled with the Gnome-based Ubuntu, but it was no big deal. The font is named console8x16 and it comes with Kubuntu’s (and KDE’s) default terminal emulator, Konsole. So I downloaded [an appropriate package][3] (manually, without the help of APT, because all I wanted was the font, not the package itself). I then installed Midnight Commander (which I use a lot, if only for its great vfs feature, which allows to access, inter alia, Debian/Ubuntu packages as if they were directories), grabbed the file `console8x16.pcf.gz`, installed it in `/usr/share/fonts/X11/misc`, changed to that directory, ran `mkfontdir` and `mkfontscale`, logged out and restarted the X server.

The last step was to use this font for Emacs, too. So I installed Emacs, created the file `~/.Xdefaults` containing the single line

```nohighlight
Emacs*font: -misc-console-medium-r-normal--16-160-72-72-c-80-iso10646-1
```

and ran `xrdb ~/.Xdefaults`.

Then I got round to configuring Emacs itself. But that’s a story for another post.

 [1]: https://wiki.ubuntu.com/HardyHeron
 [2]: http://www.antigrain.com/research/font_rasterization
 [3]: http://packages.ubuntu.com/hardy/konsole
