package Part1;

import java.io.*;
//import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        System.out.print("Enter an expression: ");
        try {
            System.out.println((new CalcEv(System.in)).eval());
        } catch (IOException | ParseError e) {
            System.err.println(e.getMessage());
        }
    }
}

