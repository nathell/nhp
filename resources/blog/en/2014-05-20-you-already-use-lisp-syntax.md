---
date: 2014-05-20
title: You already use Lisp syntax
categories: Clojure Lisp programming
---

**Unix Developer:** I’m not going to touch Lisp. It’s horrible!

**Me:** Why so?

**UD:** The syntax! This illegible prefix-RPN syntax that nobody else uses. And just look at all these parens!

**Me:** Well, many people find it perfectly legible, although most agree that it takes some time to get accustomed to. But I think you’re mistaken. Lots of people are using Lisp syntax on a daily basis…

**UD:** I happen to know no one doing this.

**Me:** …without actually realizing this. In fact, I think _you_ yourself are using it.

**UD:** Wait, _what_?!

**Me:** And the particular variant of Lisp syntax you’re using is called Bourne shell.

**UD:** Now I don’t understand. What on earth does the shell have to do with Lisp?

**Me:** Just look: in the shell, you put the name of the program first, followed by the arguments, separated by spaces. In Lisp it’s exactly the same, except that you put an opening paren at the beginning and a closing paren at the end.

Shell: `run-something arg1 arg2 arg3`

Lisp: `(run-something arg1 arg2 arg3)`

**UD:** I still don’t get the analogy.

**Me:** Then you need a mechanism for expression composition — putting the output of one expression as an input to another. In Lisp, you just nest the lists. And in the shell?

**UD:** Backticks.

**Me:** That’s right. Or `$()`, which has the advantage of being more easily nestable. Let’s try arithmetic. How do you do arithmetic in the shell?

**UD:** `expr`. Or the Bash builtin `let`. For example,

```bash
$ let x='2*((10+4)/7)'; echo $x
4
```

**Me:** Now wouldn’t it be in line with the spirit of Unix — to have programs do just one thing — if we had one program to do addition, and another to do subtraction, and yet another to do multiplication and division?

It’s trivial to write it in C:

```c
#include <stdio.h>
#include <stdlib.h>
#include <string.h>

int main(int argc, char **argv) {
  int mode = -1, cnt = argc - 1, val, i;
  char **args = argv + 1;
  switch (argv[0][strlen(argv[0]) - 1]) {
    case '+': mode = 0; break;
    case '-': mode = 1; break;
    case 'x': mode = 2; break;
    case 'd': mode = 3; break;
  }
  if (mode == -1) {
    fprintf(stderr, "invalid math operation\n");
    return 1;
  }
  if ((mode == 1 || mode == 3) && !cnt) {
    fprintf(stderr, "%s requires at least one arg\n", argv[0]);
    return 1;
  }
  switch (mode) {
    case 0: val = 0; break;
    case 2: val = 1; break;
    default: val = atoi(*args++); cnt--; break;
  }
  while (cnt--) {
    switch (mode) {
      case 0: val += atoi(*args++); break;
      case 1: val -= atoi(*args++); break;
      case 2: val *= atoi(*args++); break;
      case 3: val /= atoi(*args++); break;
    }
  }
  printf("%d\n", val);
  return 0;
}
```

This dispatches on the last character of its name, so it can be symlinked to `+`, `-`, `x` and `d` (I picked unusual names for multiplication and division to make them legal and avoid escaping).

Now behold:

```bash
$ x 2 $(d $(+ 10 4) 7)
4
```

**UD:** Wow, this sure looks a lot like Lisp!

**Me:** And yet it’s the shell. Our two basic rules — program-name-first and `$()`-for-composition — allowed us to explicitly specify the order of evaluation, so there was no need to do any fancy parsing beyond what the shell already provides.

**UD:** So is the shell a Lisp?

**Me:** Not really. The shell is [stringly typed][1]: a program takes textual parameters and produces textual output. To qualify as a Lisp, it would have to have a composite type: a list or a cons cell to build lists on top of. Then, you’d be able to represent code as this data structure, and write programs to transform code to other code.

But the Tao of Lisp lingers in the shell syntax.

<hr>

I know I’ve glossed over many details here, like the shell syntax for redirection, globbing, subprocesses, the fact that programs have standard input in addition to command-line arguments, pipes, etc. — all these make the analogy rather weak. But I think it’s an interesting way to teach Lisp syntax to people.

 [1]: http://blog.codinghorror.com/new-programming-jargon/
