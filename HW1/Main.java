package HW1;

import java.io.*;
//import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        try {
            System.out.println((new CalcEv(System.in)).eval());
        } catch (IOException | ParseError e) {
            System.err.println(e.getMessage());
        }
    }
}

