package com.example.mal.types.specials;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.example.mal.types.coll.ListUtils;
import com.example.mal.types.coll.ListUtils.ListEvalCtx;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Vector;
import io.vavr.control.Either;

@Value.Immutable
public abstract class MalDo implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "do";
    }

    public static MalDo instance() {
        return ImmutableMalDo.builder()
                             .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "do".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.DO))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read do")));
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        // AST looks like (do a b c .. n)
        if (ast.entries()
               .length() < 2) {
            return EvalContext.error(String.format("do form should have at least 2 elements, got %s.",
                                                   ast.entries()
                                                      .length()));
        }

        final Vector<MalType> bodies = ast.entries()
                                          .pop()
                                          .toVector()
                                          .dropRight(1);
        final MalType finalBody = ast.entries()
                                     .last();

        // Eagerly evaluate bodies 0..N-1
        final Either<MalError, ListEvalCtx> results = ListUtils.evalEach(env,
                                                                         bodies);

        if (results.isLeft()) {
            return EvalContext.done(results.getLeft());
        }

        // We ignore the actual return values from the other do-bodies, but do
        // use the resulting environment
        return EvalContext.reEval(finalBody,
                                  results.get()
                                         .environment());
    }
}
