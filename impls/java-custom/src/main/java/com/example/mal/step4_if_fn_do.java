package com.example.mal;

import static com.example.mal.reader.read_str;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicReference;

import com.example.mal.env.DynamicEnvironment;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.env.Functions;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.jline.reader.EndOfFileException;
import org.jline.reader.LineReader;
import org.jline.reader.LineReader.Option;
import org.jline.reader.LineReaderBuilder;
import org.jline.reader.UserInterruptException;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

public class step4_if_fn_do {

    private static final String PROMPT = "user> ";

    private static final AtomicReference<Environment> ENV = new AtomicReference<>(DynamicEnvironment.empty());

    public static MalType READ(final String input) {
        return read_str(input);
    }

    public static EvalContext EVAL(final MalType input, final Environment env) {
        return MalType.evalWithTco(input,
                                   env);
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
        final EvalContext ctx = EVAL(ast,
                                     ENV.get());
        if (ctx.result() instanceof MalError error) {
            return String.format("MAL EVAL ERROR: '%s'",
                                 error.message());
        }
        ctx.environment()
           .forEach(env -> ENV.set(env.clearLocals()));

        return PRINT(ctx.result());
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
                                    Functions.PLUS)
                               .set(Singletons.MINUS,
                                    Functions.MINUS)
                               .set(Singletons.MULTIPLY,
                                    Functions.MULTIPLY)
                               .set(Singletons.DIVIDE,
                                    Functions.DIVIDE)
                               .set(Singletons.LIST,
                                    Functions.LIST)
                               .set(Singletons.LIST_QMARK,
                                    Functions.LIST_QMARK)
                               .set(Singletons.EMPTY_QMARK,
                                    Functions.EMPTY_QMARK)
                               .set(Singletons.COUNT,
                                    Functions.COUNT)
                               .set(Singletons.PRN,
                                    Functions.makePrn(terminal.writer()))
                               .set(Singletons.PRINTLN,
                                    Functions.makePrintln(terminal.writer()))
                               .set(Singletons.STR,
                                    Functions.STR)
                               .set(Singletons.EQ,
                                    Functions.EQ)
                               .set(Singletons.LT,
                                    Functions.LT)
                               .set(Singletons.LT_EQ,
                                    Functions.LT_EQ)
                               .set(Singletons.GT,
                                    Functions.GT)
                               .set(Singletons.GT_EQ,
                                    Functions.GT_EQ));
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
