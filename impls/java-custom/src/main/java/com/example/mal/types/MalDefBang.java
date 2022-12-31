package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalDefBang implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "def!";
    }

    public static MalDefBang instance() {
        return ImmutableMalDefBang.builder()
                                  .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "def!".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    @Override
    public MalType eval(final Environment env) {
        return MalError.of("def! is valid only as the first element of a top-level form!");
    }

    public static Tuple2<Environment, MalType> evalDefBang(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() != 3) {
            return Tuple.of(env,
                            MalError.of(String.format("def! should have 3 elements, got %s",
                                                      ast.entries()
                                                         .length())));
        }
        final MalType second = ast.entries()
                                  .get(1);
        if (!(second instanceof MalSymbol s)) {
            return Tuple.of(env,
                            MalError.of(String.format("Second element of def! should be a, got '%s'",
                                                      second.pr())));
        }
        final MalType body = ast.entries()
                                .get(2)
                                .eval(env);
        if (body instanceof MalError err) {
            return Tuple.of(env,
                            err);
        }

        return Tuple.of(env.set(s,
                                body),
                        body);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.DEF_BANG))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read def!")));
    }
}
