---
date: 2024-01-26
title: Lossy CSS compression for fun and loss (or profit)
categories: programming clojure css math
---

## What

Late last year, I had an idea that’s been steadily brewing in my head. I’ve found myself with some free time recently (it coincided with vacation, go figure), and I’ve hacked together some proof-of-concept code. Whether or not it is actually proving the concept I’m not sure, but the results are somewhat interesting, and I believe the idea is novel (I haven’t found any other implementation in the wild). So it’s at least worthy of a blog post.

I wrote `cssfact`, a lossy CSS compressor. That is, a program that takes some CSS and outputs back some other CSS that hopefully retains some (most) of the information in the input, but contains fewer rules than the original. Exactly how many rules it produces is configurable, and the loss depends on that number.

The program only works on style rules (which make up the majority of a typical CSS). It leaves the non-style rules unchanged.

[Here’s the source][1]. It’s not exactly straightforward to get it running, but it shouldn’t be very hard, either. It’s very simple – the program itself doesn’t contain any fancy logic; the actual decisions on what the output will contain are made by an external program.

If you just want to see some results, here is a sample with [my homepage][2] serving as a patient etherized upon a table. Its CSS is quite small – 55 style rules that cssfact can work on – and here’s how the page looks with various settings:

- Original: [page](https://danieljanus.pl), [CSS](https://danieljanus.pl/css/nhp.css), [source SASS](https://github.com/nathell/nhp/blob/master/src/sass/nhp.sass)
- 1 style rule: [page](https://danieljanus.pl/index1.html), [CSS](https://danieljanus.pl/css/nhp1.css) (93% information loss)
- 5 style rules: [page](https://danieljanus.pl/index5.html), [CSS](https://danieljanus.pl/css/nhp5.css) (74% information loss)
- 10 style rules: [page](https://danieljanus.pl/index10.html), [CSS](https://danieljanus.pl/css/nhp10.css) (55% information loss)
- 20 style rules: [page](https://danieljanus.pl/index20.html), [CSS](https://danieljanus.pl/css/nhp20.css) (31% information loss)
- 30 style rules: [page](https://danieljanus.pl/index30.html), [CSS](https://danieljanus.pl/css/nhp30.css) (17% information loss)

My homepage and both of my blogs all use the same CSS, so you can try to replace the CSS in your browser’s devtools elsewhere on the site and see how it looks.

## How

Three words: [binary matrix factorization](https://cs.uef.fi/~pauli/bmf_tutorial/material.html) (BMF, in the Boolean algebra).

I guess I could just stop here, but I’ll elaborate just in case it isn’t clear.

Consider a simple CSS snippet:

```css
h1, h2 {
   padding: 0;
   margin-bottom: 0.5em;
}

h1 {
   font-size: 32px;
   font-weight: bold;
}

h2 {
   font-size: 24px;
   font-weight: bold;
}
```

The first rule tells you that for all elements that match either the `h1` or `h2` selectors, the two declarations should apply.

You could visualize this CSS as a 5x2 binary matrix <i>A<sup>T</sup></i> where the _n_ columns correspond to simple selectors (i.e., without commas in them) and the _m_ rows correspond to declarations:

<style>
.css-table th { text-align: right; }
.css-table td { text-align: center; }
</style>

<table class="css-table">
        <tr><th></th><th><code>h1</code></th><th><code>h2</code></th></tr>
        <tr><th><code>padding: 0</code></th><td>1</td><td>1</td></tr>
        <tr><th><code>margin-bottom: 0.5em</code></th><td>1</td><td>1</td></tr>
        <tr><th><code>font-size: 32px</code></th><td>1</td><td>0</td></tr>
        <tr><th><code>font-size: 24px</code></th><td>0</td><td>1</td></tr>
        <tr><th><code>font-weight: bold</code></th><td>1</td><td>1</td></tr>
    </tbody>
</table>

You could also transpose the matrix, yielding _A_ with _m_ rows denoting selectors and _n_ columns denoting declarations. For my homepage’s CSS, _m_ = 60 and _n_ = 81; for bigger stylesheets, several thousand in either direction is not uncommon.

Now, linear algebra gives us algorithms to find a matrix _A′ ≈ A_ such that there exists a decomposition _A′ = B × C_, where _B_ has dimensions _m × r_, _C_ has dimensions _r × n_, and _r_ is small – typically much smaller than _m_ or _n_. So this is a way of dimensionality reduction.

In the usual algebra of real numbers, there’s no guarantee that _B_ or _C_ will themselves be binary matrices – in fact, most likely they won’t. But if we operate in Boolean algebra instead (i.e. one where 1 + 1 = 1), then both _B_ and _C_ will be binary. The flip side is that the Boolean BMF problem is NP-hard, so the algorithms found in the wild perform approximate decompositions, not guaranteed to be optimal.

But that’s okay, because lossiness is inherent in what we’re doing anyway, and it turns the binary matrices _B_ and _C_ are readily interpretable. Look again at the CSS matrix above: why is there a 1 in the top-left cell? Because at least one of the CSS rules stipulates the declaration `padding: 0` for the selector `h1`.

This is exactly the definition of matrix multiplication in the Boolean algebra. The matrix _A′_ will have a 1 at coordinates [_i, j_] iff there is at least one _k_ ∈ {1, …, _r_} such that _B_[_i_, _k_] = 1 and _C_[_k_, _j_] = 1. So the columns of _B_ and rows of _C_ actually correspond to CSS rules! Every time you write CSS, you’re actually writing out binary matrices – and the browser is multiplying them to get at the actual behaviour.

Well, not really, but it’s one way to think about it. It’s not perfect – it completely glosses over rules overlapping each other and having precedence, and treats them as equally important – but it somewhat works!

You could plug in any BMF algorithm to this approach. For cssfact, I’ve picked the code by [Barahona and Goncalves 2019](https://github.com/IBM/binary-matrix-factorization/) – sadly, I wasn’t able to find the actual paper – not because it performs spectacularly well (it’s actually dog-slow on larger stylesheets), but because I was easily able to make it work and interface with it.

## Why

Why not?

The sheer joy of exploration is reason enough, but I believe there are potential practical applications. CSS codebases have the tendency to grow organically and eventually start collapsing under their own weight, and they have to be maintained very thoughtfully to prevent that. In many CSS monstrosities found in the wild, there are much cleaner, leaner, essence-capturing cores struggling to get out.

This tool probably won’t automatically extract them for you – so don’t put it in your CI pipeline – but by perusing the CSS that it produces and cross-checking it with the input, you could encounter hints on what redundancy there is in your styles. Things like “these components are actually very similar, so maybe should be united” may become more apparent.

 [1]: https://github.com/nathell/cssfact
 [2]: https://danieljanus.pl
