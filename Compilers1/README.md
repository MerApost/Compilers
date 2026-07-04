# Compilers1

This folder contains two compiler construction projects focused on parsing, expression evaluation, lexical analysis, syntax analysis, and source-to-source translation.

## Part1 — Recursive-Descent Arithmetic Expression Evaluator

### Overview

`Part1` implements a hand-written recursive-descent parser and evaluator for arithmetic expressions.

The program reads an expression from standard input, parses it according to the implemented grammar, evaluates it, and prints the final integer result. If the input does not follow the expected grammar, the program reports a parse error.

### Supported Features

The evaluator supports:

* Integer numbers
* Parenthesized expressions
* Addition
* Subtraction
* Exponentiation using `**`
* Whitespace handling
* Parse error detection

### Parsing Approach

The parser is implemented manually in Java using recursive-descent parsing.

The main parsing methods correspond to grammar levels such as:

* `Exp` / `Exp2` for addition and subtraction
* `Term` / `Term2` for exponentiation
* `Factor` for numbers and parenthesized expressions
* `Number` for multi-digit integer parsing

Exponentiation is handled with right-associative behavior, so an expression such as:

```text
2**2**2
```

is interpreted as:

```text
2**(2**2)
```

### Main Files

```text
Part1/
├── CalcEv.java
├── Main.java
├── ParseError.java
└── README.md
```

* `CalcEv.java` contains the parser and evaluator logic.
* `Main.java` reads the expression from standard input and runs the evaluator.
* `ParseError.java` defines the parse error exception.

### How to Run

From the `Compilers1` directory:

```bash
javac Part1/*.java
java Part1.Main
```

Then enter an arithmetic expression when prompted.

---

## Part2 — String Language Translator with JFlex and Java CUP

### Overview

`Part2` implements a small compiler front-end and translation pipeline for a custom string-expression language.

The project uses:

* **JFlex** for lexical analysis
* **Java CUP** for syntax analysis
* Java semantic actions for code generation

The translation process is divided into two stages:

1. Source language → Intermediate Representation
2. Intermediate Representation → Java code

### Language Features

The custom language supports string-based expressions and operations such as:

* String literals
* Identifiers
* String concatenation using `+`
* Equality checks using `=`
* Prefix checks using `prefix`
* Suffix checks using `suffix`
* String reversal using `reverse`
* Function definitions
* Function calls
* Conditional `if / else` expressions

### Translation Pipeline

The project contains two parser specifications:

```text
parser.cup
parser2.cup
```

The first parser translates the original source language into an intermediate representation.

The second parser translates the intermediate representation into Java code, generating a Java class named:

```text
Translated.java
```

The Makefile automates this flow by generating the lexer/parser files, compiling the project, and running the two translation stages.

### Main Files

```text
Part2/
├── scanner.flex
├── parser.cup
├── parser2.cup
├── Main.java
├── Main2.java
├── Makefile
├── input.txt
└── README.md
```

* `scanner.flex` defines the lexical rules of the language.
* `parser.cup` parses the source language and produces an intermediate representation.
* `parser2.cup` parses the intermediate representation and produces Java code.
* `Main.java` runs the first parser.
* `Main2.java` runs the second parser.
* `Makefile` automates compilation and execution.

### How to Run

From the `Part2` directory:

```bash
make compile
make execute < input.txt
```

The execution process generates:

```text
Translated.ir
Translated.java
```

### Current Status

The source-language to intermediate-representation stage is implemented.

The intermediate-representation to Java stage is also implemented, but the project documents a known limitation: in some cases involving function definitions and conditional expressions, the generated Java code may contain an extra `return` or an extra semicolon. This limitation is documented clearly so that the current implementation status is transparent.

### What This Project Demonstrates

This part demonstrates practical experience with:

* Lexer construction using JFlex
* Parser construction using Java CUP
* Grammar design
* Semantic actions
* Intermediate representation generation
* Source-to-source translation
* Basic compiler pipeline automation using Makefiles
