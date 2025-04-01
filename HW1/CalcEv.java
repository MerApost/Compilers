package HW1;
import java.io.*;
// import java.io.InputStream;
// import java.io.IOException;

/*
* --------------------------------------------------------------------------------------------------------
*        | '0' - '9'     |  '+'        |  '-'        |  '('          |  ')'   |  '**'           |  $      |
* --------------------------------------------------------------------------------------------------------|
*  Exp   | Term Exp2     | error       | error       | Term Exp2     | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*  Exp2  | error         | + Term Exp2 | - Term Exp2 | error         |   e    | error           |    e    |
* --------------------------------------------------------------------------------------------------------|
*  Term  | Factor Term2  | error       | error       |  Factor Term2 | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*  Term2 | error         |   e         |   e         | error         |   e    | ** Factor Term2 |   e     |
* --------------------------------------------------------------------------------------------------------|
*  Factor| num           | error       | error       | ( Exp )       | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*  Num   | digit num2    | error       | error       | error         | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*  Num2  | digit num2    |   e         |   e         |   e           |   e    |   e             |    e    |
* --------------------------------------------------------------------------------------------------------|
*  Digit | 0-9           | error       | error       | error         | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*/


public class CalcEv {
    private final InputStream in;

    private int lookahead;

    public CalcEv(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
        SkipSpace();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol){
            lookahead = in.read();
            SkipSpace();
        }
        else
            throw new ParseError();
    }

    private void SkipSpace() throws IOException {
        while (lookahead == ' ' || lookahead == '\t') {
            lookahead = in.read();
        }
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }

    // private int evalDigit(int c) {
    //     return c - '0';
    // }

    public int eval() throws IOException, ParseError {
        int value = Exp();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    //Exp -> Term Exp2
    private int Exp() throws IOException, ParseError {
        int condition = Term();
        return Exp2(condition);
    }

    //Exp2 -> (+ Term Exp2) | (-Term Exp2) | ε
    private int Exp2(int condition) throws IOException, ParseError {
        switch (lookahead) {
            case '+':
                consume('+');
                return Exp2(condition + Term());
            case '-':
                consume('-');
                return Exp2(condition - Term());
            case -1:
            case '\n':
            case ')':
                return condition;
        }

        return condition;
    }

    //Term -> Factor Term2
    private int Term() throws IOException, ParseError {
        int condition = Factor();
        return Term2(condition);
    }

    //Term2 -> (** Factor Term2) | ε
    private int Term2(int condition) throws IOException, ParseError {
        switch (lookahead){
            case '*':
                consume('*');
                if (lookahead == '*') {
                    consume('*');
                    int right = Term2(Factor());
                    return (int) Math.pow(condition, right);
                } 
                else {
                    throw new ParseError();
            }
        }
        return condition;
    }

    //Factor -> num | ε
    private int Factor() throws IOException, ParseError {
        switch (lookahead) {
            case '(':
                consume('(');
                int result = Exp();
                consume(')');
                return result;
            default:
                if (isDigit(lookahead)) {
                    return Number();
                }
                throw new ParseError();
        }
    }

    private int Number() throws IOException {
        int num = 0;
        while (isDigit(lookahead)) {
            num = num * 10 + (lookahead - '0');
            lookahead = in.read();
        }
        SkipSpace(); // Αγνοεί τυχόν κενά μετά τον αριθμό
        return num;
    }
}
