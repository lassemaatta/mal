package com.example.mal;

import static com.example.mal.reader.read_str;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.example.mal.env.DynamicEnvironment;
import com.example.mal.env.Environment;
import com.example.mal.env.Functions;
import com.example.mal.types.MalError;
import com.example.mal.types.MalFn;
import com.example.mal.types.MalType;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import io.vavr.Tuple2;

public class step3_env {

    private static final String PROMPT = "user> ";

    private static final AtomicReference<Environment> ENV = new AtomicReference<>(DynamicEnvironment.empty());

    public static MalType READ(final String input) {
        return read_str(input);
    }

    public static Tuple2<Environment, MalType> EVAL(final MalType input, final Environment env) {
        return input.rootEval(env);
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
        final Tuple2<Environment, MalType> result = EVAL(ast,
                                                         ENV.get());
        if (result._2() instanceof MalError error) {
            return String.format("MAL EVAL ERROR: '%s'",
                                 error.message());
        }
        ENV.set(result._1());
        return PRINT(result._2());
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

        // Populate the initial environment
        ENV.updateAndGet(e -> e.set(Singletons.PLUS,
                                    MalFn.of("+",
                                             Functions::plus))
                               .set(Singletons.MINUS,
                                    MalFn.of("-",
                                             Functions::minus))
                               .set(Singletons.MULTIPLY,
                                    MalFn.of("*",
                                             Functions::multiply))
                               .set(Singletons.DIVIDE,
                                    MalFn.of("/",
                                             Functions::divide)));

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
