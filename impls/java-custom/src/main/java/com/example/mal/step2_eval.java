package com.example.mal;

import static com.example.mal.reader.read_str;

import java.io.IOException;

import com.example.mal.env.Environment;
import com.example.mal.env.RootEnv;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class step2_eval {

    private static final String PROMPT = "user> ";

    public static MalType READ(final String input) {
        return read_str(input);
    }

    public static MalType EVAL(final MalType input, final Environment env) {
        return input.eval(env);
    }

    public static String PRINT(final MalType input) {
        return input.pr();
    }

    public static String rep(final String input) {
        final MalType ast = READ(input);
        if (Singletons.CONTINUE.equals(ast)) {
            return null;
        }
        if (ast instanceof MalError error) {
            return String.format("MAL READ ERROR: '%s'",
                                 error.message());
        }
        final MalType result = EVAL(ast,
                                    RootEnv.ROOT_ENV);
        if (result instanceof MalError error) {
            return String.format("MAL EVAL ERROR: '%s'",
                                 error.message());
        }
        return PRINT(result);
    }

    public static void LOOP() throws IOException {
        final Terminal terminal = TerminalBuilder.builder()
                                                 .system(true)
                                                 .build();
        final LineReader reader = LineReaderBuilder.builder()
                                                   .terminal(terminal)
                                                   // Event expander is enabled by default
                                                   .option(Option.DISABLE_EVENT_EXPANSION,
                                                           true)
                                                   .build();

        while (true) {
            try {

                final String line = reader.readLine(PROMPT);
                // System.out.println(String.format("READ: '%s'", line));
                try {
                    final String output = rep(line);
                    if (null != output) {
                        terminal.writer()
                                .println(output);
                    }
                } catch (RuntimeException e) {
                    terminal.writer()
                            .println("Unexpected Error:" + e.getMessage());
                    e.printStackTrace();
                }

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
