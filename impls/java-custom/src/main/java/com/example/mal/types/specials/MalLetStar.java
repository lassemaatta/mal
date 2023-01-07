package com.example.mal.types.specials;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.coll.MalList;
import com.example.mal.types.coll.MalSequential;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Either;

@Value.Immutable
public abstract class MalLetStar implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "let*";
    }

    public static MalLetStar instance() {
        return ImmutableMalLetStar.builder()
                                  .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "let*".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.LET_STAR))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read let*")));
    }

    @Override
    public EvalContext eval(final Environment env) {
        return EvalContext.error("let* is valid only as the first element of a form!");
    }

    private static Either<MalError, Environment> evalAndBind(final Environment env, final Seq<MalType> binding) {
        if (!(binding.head() instanceof MalSymbol s)) {
            return Either.left(MalError.of(String.format("First elements of let* bindings should be symbols, got '%s'",
                                                         binding.head()
                                                                .pr())));
        }

        final EvalContext ctx = MalType.evalWithTco(binding.get(1),
                                                    env);
        if (ctx.result() instanceof MalError err) {
            return Either.left(err);
        }

        return Either.right(ctx.environment()
                               .getOrElse(env)
                               .setLocal(s,
                                         ctx.result()));
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() != 3) {
            return EvalContext.error(String.format("let* should have 3 elements, got %s",
                                                   ast.entries()
                                                      .length()));
        }
        final MalType second = ast.entries()
                                  .get(1);
        if (!(second instanceof MalSequential<?> bindings)) {
            return EvalContext.error(String.format("Second element of let* should be a list or vector, got '%s'",
                                                   second.pr()));
        }
        final int bindingCount = bindings.entries()
                                         .length();
        if (bindingCount % 2 != 0 || bindingCount == 0) {
            return EvalContext.error(String.format("Second element of let* should have an even number of bindings, got '%s'",
                                                   bindings.pr()));
        }

        final Either<MalError, Environment> newEnvEith = bindings.entries()
                                                                 .sliding(2,
                                                                          2)
                                                                 .foldLeft(Either.right(env),
                                                                           (currentEnvEith,
                                                                            binding) -> currentEnvEith.flatMap(currentEnv -> evalAndBind(currentEnv,
                                                                                                                                         binding)));

        // Evaluating the bindings caused an error
        if (newEnvEith.isLeft()) {
            return EvalContext.done(newEnvEith.getLeft());
        }

        return newEnvEith.map(newEnv -> EvalContext.reEval(ast.entries()
                                                              .get(2),
                                                           newEnv))
                         .get();
    }
}
