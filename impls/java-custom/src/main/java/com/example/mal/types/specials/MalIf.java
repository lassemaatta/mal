package com.example.mal.types.specials;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
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
public abstract class MalIf implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "if";
    }

    public static MalIf instance() {
        return ImmutableMalIf.builder()
                             .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "if".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.IF))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read do")));
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        // AST looks like (if condition then-body else-body)
        final int entryCount = ast.entries()
                                  .length();
        if (!(entryCount == 3 || entryCount == 4)) {
            return EvalContext.error(String.format("if form should have 2 or 3 elements, got %s.",
                                                   ast.entries()
                                                      .length()));
        }

        final EvalContext conditionResult = MalType.evalWithTco(ast.entries()
                                                                   .get(1),
                                                                env);

        if (conditionResult.result() instanceof MalError err) {
            return conditionResult;
        }

        if (conditionResult.result()
                           .isTruthy()) {
            return EvalContext.reEval(ast.entries()
                                         .get(2),
                                      conditionResult.environment()
                                                     .getOrElse(env));
        } else {
            return EvalContext.reEval(entryCount == 4 ? ast.entries()
                                                           .get(3)
                    : Singletons.NIL,
                                      conditionResult.environment()
                                                     .getOrElse(env));
        }
    }
}
