---
title: "Reactivation (and some ramblings on my blogging infrastructure)"
date: 2010-01-18
---

This blog has not seen content updates in more than a year. Plenty of things can happen in such a long period, and in fact many aspect of my life have seen major changes over this time. I’m not, however, going to write a lengthy post about all that right now. Instead, I just would like to announce the reactivation of the blog.

You might have noticed that many things have changed. First, the blog has a new address: [`http://blog.danieljanus.pl`][1]; the address of the RSS feed has also changed and is now [`http://blog.danieljanus.pl/index.rss`][2] — please update your readers!

Probably the most important change is that you now may post comments under the entries, even though this blog continues to be just a bunch of static HTML pages. This is possible thanks to the [Disqus][3] service. I wonder whether it will encourage people to give feedback: I have received very few email comments since I started blogging. Also, the static calendar at the top of each page is gone, replaced by a bunch of links to archive posts.

I have long been considering changing [Blosxom][4] to something else. The main reason for such a step is that it’s written in Perl, which makes it particularly hard to debug upon encountering an unexpected behaviour. The single most irritating thing was that Blosxom would unexpectedly change the date of a post that was edited (which did not let me fix typos and other glitches); I found a patch for this somewhere, but lost it.

On the other hand, I really liked — and still like — Blosxom’s minimalistic approach and the ease of adding posts. (The very idea of installing a monstrosity such as Wordpress, with its gazillion of features I don’t need, posts kept in a database and what not, makes me feel dizzy.) I fiddled for a while with the thought of reimplementing Blosxom in Common Lisp, but that turned out to be a more time-consuming project than it initially seemed. So when I found [The Unofficial Blosxom User Group][5] and learned that, contrary to my belief, Blosxom is still actively maintained and has a thriving community, I ended up staying with the original Perl version, refining my installation so that it no longer gets in the way ([this FAQ entry][6] did the trick). I also rewrote all my source text files to [Markdown][7], which made them vastly more readable and easy to edit, updating links and adding short followup notes where appropriate, but otherwise leaving old entries as they were.

I’d like to thank [Maciek Pasternacki][8] for inspiring me to finally get around to this. While my plans are not as ambitious as his — I am not courageous enough to publicly prove my perseverance, so my blogging will likely continue to be irregular — I plan to write more (having accumulated many ideas for blog posts) and I hope the periods of silence will be much shorter than hitherto.

I would like to take this opportunity to wish my readers all the best in the New Year!

 [1]: http://blog.danieljanus.pl
 [2]: http://blog.danieljanus.pl/index.rss
 [3]: http://disqus.com/
 [4]: http://www.blosxom.com/
 [5]: http://blosxom.ookee.com/
 [6]: http://blosxom.ookee.com/blog/help/howto_update_posts_without_making_the_date_change.html
 [7]: http://daringfireball.net/projects/markdown/
 [8]: http://www.3ofcoins.net/2010/01/08/revive-the-blog-project-52/
