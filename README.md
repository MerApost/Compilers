# Compilers

This repository contains a collection of compiler construction projects implemented in Java.
The projects cover several core compiler concepts, including hand-written parsing, lexical analysis, syntax analysis, intermediate representation generation, source-to-source translation, symbol table construction, inheritance handling, and semantic/type checking.

## Repository Structure

```text
Compilers/
├── Compilers1/
│   ├── Part1/
│   └── Part2/
└── Compilers2/
```

## Projects

### Compilers1

`Compilers1` contains two smaller compiler front-end projects.

* **Part1** implements a hand-written recursive-descent parser and evaluator for arithmetic expressions.
* **Part2** implements a lexer/parser-based translator for a small string-expression language using **JFlex** and **Java CUP**. It follows a two-stage translation pipeline: source language to intermediate representation, and intermediate representation to Java code.

### Compilers2

`Compilers2` implements a semantic analyzer for **MiniJava**.
It builds a symbol table, checks declarations and type rules, handles inheritance-related semantic checks, and supports field/method offset calculation.

## Technologies Used

* Java
* JFlex
* Java CUP
* JavaCC
* JTB
* Recursive-descent parsing
* Abstract Syntax Tree visitors
* Symbol tables
* Semantic analysis
* Type checking
* Intermediate representation generation

## Key Learning Outcomes

Through these projects, I worked on important compiler construction techniques, such as:

* Designing and implementing grammar-based parsers
* Building a parser manually using recursive descent
* Using lexer and parser generators
* Translating a small source language into an intermediate representation
* Generating Java code from an intermediate representation
* Building symbol tables for object-oriented programs
* Implementing semantic checks for classes, methods, variables, inheritance, and expressions
* Validating type correctness in a MiniJava-like language

## Purpose

This repository was developed as part of my compiler construction coursework and demonstrates my understanding of how programming languages are parsed, analyzed, and translated by compiler front-end tools.
