---
date: 2008-04-24
title: Hacking away with JSON-RPC
categories: Lisp Poliqarp programming
---

Let’s try:

```lisp
(let ((s (socket-stream
          (socket-connect "localhost" 10081
                          :element-type '(unsigned-byte 8)))))
  (write-netstring "{\"method\":\"ping\",\"params\":[],\"id\":1}" s)
  (finish-output s)
  (princ (read-netstring s))
  (close s))
; { "result": "pong" }
; --> T
```

Yay! This is Common Lisp talking to a [JSON-RPC][1] server written in C. This means that I have now the foundations for rewriting Poliqarp on top of JSON-RPC (according to the [protocol spec][2] I have recently posted) up and running, and all that remains is to fill the remainder.

Well, to be honest, this is not exactly JSON-RPC. First off, as you might have noticed, the above snippet of code sends JSON-RPC requests as [netstrings][3]. This is actually intentional, and the reasons for adopting this encoding have been described in detail in the spec (it basically boils down to the fact that it greatly simplifies reading from and writing to network, especially in C). I wrote some crude code to handle netstrings in CL — now it occurred to me that it might actually be worthwhile to polish it up a little, write some documentation and put on [CLiki][4] as an asdf-installable library. I’ll probably get on to this quite soon.

Second, the resulting JSON object does not have all the necessary stuff. It contains the result, but not the error or id (as mandated by the [JSON-RPC spec][5]). This is actually a deficiency of the [JSON-RPC C library][6] I’m currently using. It places the burden of constructing objects that are proper JSON-RPC responses on the programmer, instead of doing that itself. This will be easy to sort out, however, because the library is small and adheres to the [KISS principle][7]. More of a problem is that the licensing of that library is unclear; I emailed the maintainers to explain the status.

 [1]: http://json-rpc.org/
 [2]: http://blog.danieljanus.pl/poliqarp-new-protocol.html
 [3]: http://cr.yp.to/proto/netstrings.txt
 [4]: http://www.cliki.net/
 [5]: http://json-rpc.org/wiki/specification
 [6]: http://www.big-llc.com/software.jsp
 [7]: http://en.wikipedia.org/wiki/KISS_principle
