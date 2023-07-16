---
title: Downcasing strings
date: 2010-02-16
categories: Unix
---

I just needed to convert a big (around 200 MB) text file, encoded in UTF-8 and containing Polish characters, all into lowercase. `tr` to the rescue, right? Well, not quite.

```
$ echo ŻŹŚÓŃŁĘĆĄ | tr A-ZĄĆĘŁŃÓŚŹŻ a-ząćęłńóśźż
żźśóńłęćą
```

Looks reasonable (apart from the fact that I need to specify an explicit character mapping — it would be handy to just have a lcase utility or suchlike); but here’s what happens on another random string:

```
$ echo abisyński | tr A-ZĄĆĘŁŃÓŚŹŻ a-ząćęłńóśźż
abisyŅski
```

I was just about to report this as a bug, when I spotted the following in the manual:

> Currently `tr` fully supports only single-byte characters. Eventually it will support multibyte characters; when it does, the `-C` option will cause it to complement the set of characters, whereas `-c` will cause it to complement the set of values.

Turns out some of the basic tools don’t support multibyte encodings. `dd conv=lcase`, for instance, doesn’t even pretend to touch non-ASCII letters, and perl’s `tr` operator likewise fails miserably even when one specifies `use utf8`.

This is a sad, sad state of affairs. It’s 2010, UTF-8 has been around for seventeen years, and it’s still not supported by one of the core operating system components as other encodings are becoming more and more obsolete. I’m dreaming of the day my system uses it internally for everything.

Fortunately, not everything is broken. Gawk, for example, works:

```
$ echo koŃ i żÓłw | gawk '{ print tolower($0); }'
koń i żółw
```

and so does sed.

_Update 2010-04-04:_ I should have been more specific. The above rant applies to the GNU tools (`tr` and `dd`) as found in most Linux distributions; other versions can be more featureful. As [Alex Ott][1] points out in an email comment, tr on OS X works as expected for characters outside of ASCII, and also supports character classes as in `tr '[:upper:]' '[:lower:]'`. This is yet another testimony to general high quality of Apple software; in this particular case, though, it may well be a direct effect of OS X’s BSD heritage. Does it work on *BSD?

 [1]: http://alexott.net/
