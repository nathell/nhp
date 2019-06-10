---
title: A quirk with JavaScript closures
date: 2011-05-15
categories: JavaScript programming
---

I keep running into this obstacle every now and then. Consider this example:

```javascript
> q = []
[]
> for (var i = 0; i < 3; i++)
    q.push(function() { console.log(i); });
> q[0]()
3
```

I wanted an array of three closures, each printing a different number to the console when called. Instead, each prints 3 (or, rather, whatever the value of the variable `i` happens to be).

I am not exactly sure about the reason, but presumably this happens because the `i` in each lambda refers to the _variable_ `i` itself, not to its binding from the creation time of the function.

One solution is to enforce the bindings explicitly on each iteration, like this:

```javascript
for (var i = 0; i < 3; i++)
  (function(v) {
    q.push(function() { console.log(v); });
  })(i);
```

Or use [Underscore.js][1], which is what I actually do:

```javascript
_([1,2,3]).each(function(i) {
  q.push(function() { console.log(i); });
});
```

 [1]: http://documentcloud.github.com/underscore/
