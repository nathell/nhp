---
title: Sunflower
date: 2010-04-18
categories: Clojure programming
---

The program I’ve been [writing about recently][1] has come to a point where I think it can be shown to the wide public. It’s called [Sunflower][2] and has its home on GitHub. It’s nowhere near being completed, and of alpha quality right now, but even at this stage it might be useful.

Just as sunflower seed kernels come wrapped in hulls, most HTML documents seen in the wild come wrapped in noise that is not really part of the document itself. Take any news site: a document from such a site contains things such as advertisements, header, footer, and many links. Now suppose you have many documents grabbed from the same site. Is it possible to somehow automate the extraction of the document “essences”?

Sunflower to the rescue. It relies on the assumption that documents coming from the same source have the same structure. It presents a list of strings to the user, and asks to pick those that are contained in the text essence. Then it finds the coordinates of the smallest HTML subtree that contains all those strings, and uses those coordinates to extract information from all documents. And it comes with a nice, easily understandable GUI for that.

This technique works remarkably well for many collections, although not all. An earlier, proof-of-concept implementation (in Common Lisp) has been used to extract many press texts for the [National Corpus of Polish][3].

I’ve given up on the symbol-capturing approach to wizards I’ve presented in my previous posts. Inspired by the DOM tree in Web apps, with a bag of elements with identifiers, I now have a central bag of Swing widgets (implemented as an atom) identified by keywords. This bag contains tidbits of the mutable state of Sunflower. This means that I can write callback functions like this:

```clojure
#(with-components [strings-model selected-dir]
   (.removeAllElements strings-model)
   (let [p (-> selected-dir htmls first parse)]
     (add-component :parsed p)
     (doseq [x (strings p)]
       (.addElement strings-model x))))
```

Name and conquer: having parts of state explicitly named mean that I can reliably access them from just about anywhere. This reduces confusion and allows for less tangled, more self-contained and understandable code.
