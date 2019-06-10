---
title: Meet my little friend createTree
date: 2011-07-08
categories: JavaScript Mobile-Safari iPhone programming
---

I’ve recently been developing an iPhone application in my spare time. I’m not going to tell you what it is just yet (I will post a separate entry once I manage to get it into the App Store); for now, let me just say that I’m writing it in JavaScript and HTML5, using [PhoneGap][1] and [jQTouch][2] to give it a native touch.

After having written some of code, I began testing it on a real device and encountered a nasty issue. It turned out that some of the screens of my app, containing a dynamically-generated content, sometimes would not show up. I tried to chase the problem down, but it seemed totally random. Finally, I googled up [this blog post][3] that gave me a clue.

My code was using jQuery’s `.html()` method (and hence `innerHTML` under the hood) to display the dynamic content. It turns out that, on Mobile Safari, using `innerHTML` is highly unreliable (at least on iOS 4.3, but this seems to be a long-standing bug). Sometimes, the change just does not happen. I changed one of my screens, to build and insert DOM objects explicitly, and sure enough, it started to work predictably well.

So I had to remove all usages of `.html()` from my app. The downside to it was that explicit DOM-building code is much more verbose than the version that constructs HTML and then sets it up. It’s tedious to write and contains much boilerplate.

To not be forced to change code, the above-quoted article advocates using a pure-JavaScript HTML parser outputting DOM to replace jQuery’s `.html()` method. I considered this for a while, but finally decided against it — I didn’t want to include another big, complex dependency that potentially could misbehave at times (writing HTML parsers is _hard_).

Instead, I came up with this:

```javascript
function createTree(tree) {
    if (typeof tree === 'string' || typeof tree === 'number')
        return document.createTextNode(tree);
    var tag = tree[0], attrs = tree[1], res = document.createElement(tag);
    for (var attr in attrs) {
        val = attrs[attr];
        if (attr === 'class')
            res.className = val;
        else
            $(res).attr(attr, val);
    }
     for (var i = 2; i < tree.length; i++)
        res.appendChild(createTree(tree[i]));
    return res;
}
```

This is very similar in spirit to `.html()`, except that instead of passing HTML, you give it a data structure representing the DOM tree to construct. It can either be a string (which yields a text node), or a list consisting of the HTML tag name, an object mapping attributes to their values, and zero or more subtrees of the same form. Compare:

Using `.html()`:

```javascript
var html = '<p>This is an <span class="red">example.</span></p>';
$('#myDiv').html(html);
```

Using `createTree`:

```javascript
var tree = ['p', {},
            'This is an ',
            ['span', {'class': 'red'}, 'example.']];
$('#myDiv').empty().append(createTree(tree));
```

A side benefit is that it is just as easy to build up a tree dynamically as it is to create HTML, and the code often gets clearer. Note how the `createTree` version above does not mix single and double quotes which is easy to mess up in the `.html()` version.
