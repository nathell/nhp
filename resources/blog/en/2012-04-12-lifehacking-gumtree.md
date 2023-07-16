---
date: 2012-04-12
title: "Lifehacking: How to get cheap home equipment using Clojure"
categories: Clojure lifehacking programming
---

I’ve moved to London last September. Like many new Londoners, I have changed accommodation fairly quickly, being already after one removal and with another looming in a couple of months; my current flat was largely unfurnished when I moved in, so I had to buy some basic homeware. I didn’t want to invest much in it, since it’d be only for a few months. Luckily, it is not hard to do that cheaply: many people are moving out and getting rid of their stuff, so quite often you can search for the desired item on [Gumtree][1] and find there’s a cheap one a short bike ride away.

Except when there isn’t. In this case, it’s worthwhile to check again within a few days as new items are constantly being posted. Being lazy, I’ve decided to automate this. A few hours and a hundred lines of Clojure later, [gumtree-scraper][2] was born.

I’ve packaged it using `lein uberjar` into a standalone jar, which, when run, produces a `gumtree.rss` that is included in my Google Reader subscriptions. This way, whenever something I’m interested in appears, I get notified within an hour or so.

It’s driven by a Google spreadsheet. I’ve created a sheet that has three columns: item name, minimum price, maximum price; then I’ve made it available to anyone who knows the URL. This way I can edit it pretty much from everywhere without touching the script. Each time the script is run (by cron), it downloads that spreadsheet as a CSV that looks like this:

```
hand blender,,5
bike rack,,15
```

For each row the script queries Gumtree’s category “For Sale” within London given the price range, gets each result and transforms it to a RSS entry.

Gumtree has no API, so I’m using screenscraping to retrieve all the data. Because the structure of the pages is much simpler, I’m actually scraping the [mobile version][3]; a technical twist here is that the mobile version is only served to actual browsers so I’m supplying a custom User-Agent, pretending to be Safari. For actual scraping, the code uses [Enlive][4]; it works out nicely.

About half of the code is RSS generation — mostly XML emitting. I’d use `clojure.xml/emit` but it’s known to [produce malformed XML][5] at times, so I include a variant that should work.

In case anyone wants to tries it out, be aware that the location and category are hardcoded in the search URL template; if you want, change the template line in `get-page`. The controller spreadsheet URL is not, however, hardcoded; it’s built up using the `spreadsheet.key` system property. Here’s the wrapper script I use that is actually run by cron:

```bash
#!/bin/bash
if [ "`ps ax | grep java | grep gumtree`" ]; then
  echo "already running, exiting"
  exit 0
fi
cd "`dirname $0`"
java -Dspreadsheet.key=MY_SECRET_KEY -jar $HOME/gumtree/gumtree.jar
cp $HOME/gumtree/gumtree.rss $HOME/public_html
```

Now let me remove that entry for a blender — I’ve bought one yesterday for £4…

 [1]: http://www.gumtree.com/london
 [2]: https://github.com/nathell/gumtree-scraper
 [3]: http://m.gumtree.com/
 [4]: https://github.com/cgrand/enlive
 [5]: http://clojure-log.n01se.net/date/2012-01-03.html#17:28a
