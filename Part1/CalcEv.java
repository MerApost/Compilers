package Part1;
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
*  Digit | 0-9           | error       | error       | error         | error  | error           |  error  |
* --------------------------------------------------------------------------------------------------------|
*/


public class CalcEv {
    private final InputStream in;

    private int lookahead;

    public CalcEv(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read(); // diavasma 1ou xaraktira
        SkipSpace();
    }

    private void consume(int symbol) throws IOException, ParseError { //pairnoume ton xaraktira poy 8eloyme, allios parseerror
        if (lookahead == symbol){
            lookahead = in.read();
            SkipSpace();
        }
        else
            throw new ParseError();
    }

    private boolean isDigit(int c) { //elegxoume an o xaraktiras einai psifio
        return '0' <= c && c <= '9';
    }

    // private int evalDigit(int c) {
    //     return c - '0';
    // }

    public int eval() throws IOException, ParseError {
        int value = Exp();

        if (lookahead != -1 && lookahead != '\n') //elegxos an yparxoun xaraktires poy den diavase
            throw new ParseError();

        return value;
    }

    //Exp -> Term Exp2
    private int Exp() throws IOException, ParseError {
        int condition = Term();  //diavazei Term
        return Exp2(condition);  //kanei + or - meso Expr2
    }

    //Exp2 -> (+ Term Exp2) | (-Term Exp2) | ε
    private int Exp2(int condition) throws IOException, ParseError {
        switch (lookahead) {
            case '+':
                consume('+');
                return Exp2(condition + Term());  //kanei thn pros8esh
            case '-':
                consume('-');
                return Exp2(condition - Term());  //kanei thn afairesh
            case -1:   //se ka8e alli periptosh {-1,),\n}
            case '\n':
            case ')':
                return condition; //epistrefei to apotelesma
        }

        return condition; 
    }

    //Term -> Factor Term2
    private int Term() throws IOException, ParseError {
        int condition = Factor();  //diavazei Factor
        return Term2(condition);   //kanei prakseis **
    }

    //Term2 -> (** Factor Term2) | ε
    private int Term2(int condition) throws IOException, ParseError {
        switch (lookahead){
            case '*':
                consume('*');
                if (lookahead == '*') {  //prepei na einai **, allios an einai apla * parseerror
                    consume('*');
                    int condition2 = Term2(Factor());  //giati 8eloume na kanei prajeiw apo deksia
                    return (int) Math.pow(condition, condition2); //px 2**2**2 =>  2**(2**2)
                }                                               // h Math.pow(x,y) ypologizei thn dynami(x**y)
                else {
                    throw new ParseError();
            }
        }
        return condition;
    }

    //Factor -> num | (Exp)
    private int Factor() throws IOException, ParseError {
        switch (lookahead) {
            case '(':    //diavazei Exp entos paren8eseon
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

    //diavasma ari8mou (me polla psifia)
    private int Number() throws IOException {
        int num = 0;
        while (isDigit(lookahead)) {
            num = num * 10 + (lookahead - '0');   //kanei to string => ari8mo
            lookahead = in.read();
        }
        SkipSpace();
        return num;
    }

    private void SkipSpace() throws IOException { //prospername ta kena
        while (lookahead == ' ' || lookahead == '\t') {
            lookahead = in.read();
        }
    }
}
