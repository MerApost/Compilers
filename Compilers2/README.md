# Compilers2 — MiniJava Semantic Analyzer

## Overview

`Compilers2` implements a semantic analyzer for **MiniJava**, a simplified Java-like language commonly used in compiler construction courses.

The project parses MiniJava programs, builds a symbol table, and performs semantic/type checking in order to detect invalid programs before code generation.

## Main Goal

The goal of this project is to implement the semantic analysis phase of a compiler front-end.

The analyzer checks whether a MiniJava program is semantically valid by verifying classes, inheritance, fields, methods, variables, expressions, statements, and method calls.

## Analysis Pipeline

The project follows a multi-stage compiler front-end pipeline:

1. Parse the MiniJava input program.
2. Build the Abstract Syntax Tree using generated parser/visitor infrastructure.
3. Perform a first visitor pass to build the symbol table.
4. Perform a second visitor pass to type-check the program.
5. Report semantic errors when the program violates MiniJava rules.

## Two-Pass Semantic Analysis

### First Pass — Symbol Table Construction

The first visitor pass collects program declarations and stores them in a symbol table.

It records information about:

* Classes
* Parent classes / inheritance relationships
* Fields
* Methods
* Method return types
* Method parameters
* Local variables

This pass also detects declaration-related errors such as duplicate classes, duplicate fields, duplicate methods, duplicate parameters, and invalid inheritance references.

### Second Pass — Type Checking

The second visitor pass validates the actual program logic using the symbol table created during the first pass.

It checks whether expressions, assignments, method calls, return statements, conditions, and array operations are type-correct.

## Implemented Semantic Checks

The analyzer supports checks such as:

* Duplicate class declarations
* Duplicate field declarations
* Duplicate method declarations
* Duplicate method parameters
* Duplicate local variable declarations
* Undefined classes
* Undefined variables
* Invalid inheritance references
* Invalid method overriding
* Forbidden method overloading in subclasses
* Invalid assignment types
* Invalid return types
* Invalid `if` and `while` condition types
* Invalid arithmetic operation operands
* Invalid boolean operation operands
* Invalid array indexing
* Invalid array length usage
* Invalid method calls
* Wrong number of method call arguments
* Wrong method call argument types
* Subclass-to-superclass assignment compatibility

## Offset Support

The symbol table also maintains offset information for fields and methods.

This is useful for later compiler stages, such as object layout and virtual table construction.

The implementation handles different field sizes, such as:

* `int`
* `boolean`
* Object references
* Arrays

## Project Structure

```text
Compilers2/
├── minijava_example/
│   ├── Main.java
│   ├── MyVisitor.java
│   ├── SymbolTable.java
│   ├── TypeCheck.java
│   ├── MiniJavaParser.java
│   ├── Makefile
│   ├── syntaxtree/
│   └── visitor/
├── minijava-examples-new/
├── jtb132di.jar
├── javacc5.jar
└── README.md
```

## Important Files

* `Main.java` runs the parser and starts the semantic analysis passes.
* `MyVisitor.java` implements the first pass and builds the symbol table.
* `SymbolTable.java` stores classes, fields, methods, variables, inheritance information, and offsets.
* `TypeCheck.java` implements the second pass and performs semantic/type checking.
* `minijava-examples-new/` contains example MiniJava programs used for testing, including valid examples and examples with semantic errors.
* `Makefile` automates parser generation and Java compilation.

## How to Run

From the `Compilers2/minijava_example` directory:

```bash
make
java Main ../minijava-examples-new/BinaryTree.java
```

To clean generated files:

```bash
make clean
```

## Technologies Used

* Java
* JavaCC
* JTB
* Visitor pattern
* Symbol table design
* Semantic analysis
* Type checking
* Inheritance validation

## What This Project Demonstrates

This project demonstrates the implementation of an important compiler phase: semantic analysis.

It shows how a compiler can move beyond syntax checking and verify whether a program is meaningful and type-safe according to the rules of the language. The project also demonstrates object-oriented language analysis concepts such as classes, methods, inheritance, overriding, variable scopes, and method call validation.

