# Compiler Construction
The Cambridge Computer Science BA degree contains a Compiler Construction course, which teaches the main concepts associated with implementing compilers for programming languages. This course is extended by the Optimising Compilers course in Part II.

This directory contains my toy compiler implementations:
- **Pocket calculator**: A very basic implementation of a pocket calculator, taking in an expression as a string and outputting the numeric value of the string. The calculator supports a small number of operators with different associativities and precedence levels, showing how these properties are encoded into the grammar for a language. This project contains a useful example of an LR(0) parser, indicating how associativity can be implied by the parse tree when using a parsing technique that can handle left-recursive grammars.
