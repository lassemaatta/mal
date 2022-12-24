package com.example.mal;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class step0_repl {

    private static final String PROMPT = "user> ";

    public static String READ(final String input) {
        return input;
    }

    public static String EVAL(final String input) {
        return input;
    }

    public static String PRINT(final String input) {
        return input;
    }

    public static String rep(final String input) {
        return PRINT(EVAL(READ(input)));
    }

    public static void LOOP() {
        try (BufferedReader r = new BufferedReader(new InputStreamReader(System.in))) {
            while (true) {
                System.out.print(PROMPT);
                final String line = r.readLine();
                if (null == line) {
                    System.out.println();
                    System.exit(0);
                }
                System.out.println(rep(line));
            }

        } catch (final IOException ex) {
            System.err.print(ex.toString());
        }
    }

    public static void main(final String[] args) {
        LOOP();
    }
}
