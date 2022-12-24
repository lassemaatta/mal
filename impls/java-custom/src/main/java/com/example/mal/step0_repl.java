package com.example.mal;

import java.io.IOException;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

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

    public static void LOOP() throws IOException {
        final Terminal terminal = TerminalBuilder.terminal();
        final LineReader reader = LineReaderBuilder.builder()
                                                   .terminal(terminal)
                                                   .build();

        while (true) {
            try {
                final String line = reader.readLine(PROMPT);
                final String output = rep(line);
                terminal.writer()
                        .println(output);
            } catch (UserInterruptException e) {
                System.exit(1);
            } catch (EndOfFileException e) {
                System.exit(0);
            }
        }
    }

    public static void main(final String[] args) throws IOException {
        LOOP();
    }
}
