package HW1;
import java.io.*;
// import java.io.InputStream;
// import java.io.IOException;
/*
* -------------------------------------------------------------------------
* 	        |     '0' .. '9'     |  ':'    |       '?'          |  $    |
* -------------------------------------------------------------------------
* 	        |		             |	       |	                |       |
* Tern      | '0'..'9' TernTail  |  error  |       error        | error |
*           | 	   	             |	       |    	            |       |
* -------------------------------------------------------------------------
*           |		             |	       |		            |       |
* TernTail  |       error	     |    e    |  '?' Tern ':' Tern |   e   |
* 	        |	  	             |	       |    	     	    |       |
* -------------------------------------------------------------------------
*/

public class CalcEv {
    private final InputStream in;

    private int lookahead;

    public CalcEv(InputStream in) throws IOException {
        this.in = in;
        lookahead = in.read();
    }

    private void consume(int symbol) throws IOException, ParseError {
        if (lookahead == symbol)
            lookahead = in.read();
        else
            throw new ParseError();
    }

    private boolean isDigit(int c) {
        return '0' <= c && c <= '9';
    }

    private int evalDigit(int c) {
        return c - '0';
    }

    public int eval() throws IOException, ParseError {
        int value = Exp();

        if (lookahead != -1 && lookahead != '\n')
            throw new ParseError();

        return value;
    }

    //Exp -> Term Exp2
    private int Exp() throws IOException, ParseError {
        // if (isDigit(lookahead)) {
        //     int cond = evalDigit(lookahead);

        //     consume(lookahead);
        //     return Exp2(cond); 
        // }

        // throw new ParseError();
        int left = Term();
        return Exp2(left);
    }

    //Exp2 -> (+ Term Exp2) | (-Term Exp2) | ε
    private int Exp2(int condition) throws IOException, ParseError {
        switch (lookahead) {
            case '+':
                consume('+');
                //int right = Exp();
                return Exp2(condition + Term());
            case '-':
                consume('-');
                //right = Exp();
                return Exp2( condition - Term());
            // case '*':
            //     consume('*');
            //     if ( lookahead == '*'){
            //         consume('*');
            //         right = Exp();
            //         return (int) Math.pow(condition, right);
            //     }
            //     throw new ParseError();
            case -1:
            case '\n':
            case ')':
                return condition;
        }

        throw new ParseError();
    }

    //Term -> Factor Term2
    private int Term() throws IOException, ParseError {
        // if (isDigit(lookahead)) {
        //     int cond = Factor();

        //     consume(lookahead);
        //     return Term2(cond); 
        // }

        // throw new ParseError();
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
                    int right = Factor();
                    return Term2((int) Math.pow(condition, right));
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
                    int num = evalDigit(lookahead);
                    consume(lookahead);
                    return num;
                }
                throw new ParseError();
        }
    }
}
