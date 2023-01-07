package com.example.mal.types.specials;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalRuntimeFn;
import com.example.mal.types.MalType;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Either;

@Value.Immutable
public abstract class MalFnStar implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "fn*";
    }

    public static MalFnStar instance() {
        return ImmutableMalFnStar.builder()
                                 .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "fn*".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.FN_STAR))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read fn*")));
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        // AST looks like (fn* (arg1 arg2 & args) (body ..))
        if (ast.entries()
               .length() != 3) {
            return EvalContext.error(String.format("fn* form should have 3 elements, got %s.",
                                                   ast.entries()
                                                      .length()));
        }

        final Either<MalError, Bindings> bindRes = Bindings.construct(ast.entries()
                                                                         .get(1));

        if (bindRes.isRight()) {
            // Bindings symbols were valid -> Return a function closure but don't eval
            // anything
            return bindRes.map(binds -> EvalContext.withEnv(MalRuntimeFn.of(binds,
                                                                            ast.entries()
                                                                               .get(2)),
                                                            env))
                          .get();
        } else {
            return bindRes.mapLeft(err -> EvalContext.done(err))
                          .getLeft();
        }

    }
}
