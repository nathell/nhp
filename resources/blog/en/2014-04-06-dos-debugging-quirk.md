---
date: 2014-04-06
title: DOS debugging quirk
categories: DOS assembly programming
---

While hacking on Lithium, I’ve noticed an interesting thing. Here’s a sample DOS program in assembly (TASM syntax):

```x86asm
.model tiny
.code
  org 100h

N equ 2

start:
  mov bp,sp
  mov ax,100
  mov [bp-N],ax
  mov cx,[bp-N]
  cmp cx,ax
  jne wrong
  mov dx,offset msg
  jmp disp
wrong:
  mov dx,offset msg2
disp:
  mov ah,9
  int 21h
  mov ax,4c00h
  int 21h

msg db "ok$"
msg2 db "wrong$"
end start
```

If you assemble, link and then execute it normally, typing `prog` in the DOS command line, it will output the string “ok”. But if you trace through the program in a debugger instead, it will say “wrong”! What’s wrong?

The problem is in lines 10-11 (instructions 3-4). Here’s what happens when you trace through this program in DOS 6.22’s `DEBUG.EXE`:

<img src="/img/blog/debug.png">

Note how in instruction 3 (actually displayed as the second above) we set the word `SS:0xFFFC` to `100`. When about to execute the following instruction, we would expect that word to continue to hold the value `100`, because nothing which could have changed that value has happened in between. Instead, the debugger still reports it as `0x0D8A`, as if instruction 3 had not been executed at all — and, interestingly, after actually executing this instruction, `CX` gets yet another value of `0x7302`!

Normally, thinking of DOS `.COM` programs, you assume a 64KB-long chunk of memory that the program has all to itself: the code starts at `0x100`, the stack grows from `0xFFFE` downwards (at any given time, the region from `SP` to `0xFFFE` contains data currently on the stack), and all memory in between is free for the program to use however it deems fit. It turns out that, when debugging, it is not the case: the debuggers need to manipulate the region just underneath the program’s stack in order to handle the tracing/breakpoint interrupt traps.

I’ve verified that both DOS’s DEBUG and Borland’s Turbo Debugger 5 do this. The unsafe-to-touch amount of space below SP that they need, however, varies. Manipulating the N constant in the original program, I’ve determined that DEBUG only needs 8 bytes below SP, whereas for TD it is a whopping 18 bytes.
