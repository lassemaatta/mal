package com.example.mal.types.atoms;

import java.util.function.Function;
import java.util.regex.Pattern;

import com.example.mal.Reader;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalSymbol implements MalType {

    private static final Pattern PATTERN =
            Pattern.compile("[\\p{Alpha}\\*\\+\\!\\-_'\\?\\<\\>=/&]+[\\p{Alnum}\\*\\+\\!\\-_'\\?\\<\\>=/&]*");

    public abstract String name();

    @Override
    @Lazy
    public String pr() {
        return name();
    }

    @Override
    public EvalContext eval(final Environment env) {
        return env.lookupValue(this)
                  .map(val -> EvalContext.withEnv(val,
                                                  env))
                  .getOrElse(EvalContext.error(String.format("Value for '%s' not found",
                                                             name())));
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() == 0) {
            return EvalContext.error("Cannot invoke empty list");
        }
        // This is just a roundabout way of calling eval on ourselves
        final EvalContext ctx = MalType.evalWithTco(ast.entries()
                                                       .head(),
                                                    env);
        final MalType head = ctx.result();
        if (head instanceof MalError) {
            return ctx;
        }

        // We've only evaluated the head, rest of the list is still un-evaluated
        // and the evaluated head can decide what to do
        final MalList newList = ast.replaceHead(head);
        return head.evalList(newList,
                             env);
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> PATTERN.matcher(token)
                                     .matches())
                .getOrElse(false);
    }

    public static MalSymbol of(final String token) {
        return ImmutableMalSymbol.builder()
                                 .name(token)
                                 .build();
    }

    public static MalSymbol read(final String token) {
        return of(token);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) read(token)))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read symbol")));
    }
}
