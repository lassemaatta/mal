package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
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
    public MalType eval(final Environment env) {
        return MalError.of("let* is valid only as the first element of a form!");
    }

    private static Either<MalError, Environment> evalAndBind(final Environment env, final Seq<MalType> binding) {
        if (!(binding.head() instanceof MalSymbol s)) {
            return Either.left(MalError.of(String.format("First elements of let* bindings should be symbols, got '%s'",
                                                         binding.head()
                                                                .pr())));
        }

        final MalType body = binding.get(1)
                                    .eval(env);
        if (body instanceof MalError err) {
            return Either.left(err);
        }
        return Either.right(env.set(s,
                                    body));
    }

    public static MalType evalLetStar(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() != 3) {
            return MalError.of(String.format("let* should have 3 elements, got %s",
                                             ast.entries()
                                                .length()));
        }
        final MalType second = ast.entries()
                                  .get(1);
        if (!(second instanceof MalCollection<?> bindings)) {
            return MalError.of(String.format("Second element of let* should be a list or vector, got '%s'",
                                             second.pr()));
        }
        final int bindingCount = bindings.entries()
                                         .length();
        if (bindingCount % 2 != 0 || bindingCount == 0) {
            return MalError.of(String.format("Second element of let* should have an even number of bindings, got '%s'",
                                             bindings.pr()));
        }

        final Either<MalError, Environment> newEnvEith = bindings.entries()
                                                                 .sliding(2,
                                                                          2)
                                                                 .foldLeft(Either.right(env),
                                                                           (currentEnvEith,
                                                                            binding) -> currentEnvEith.flatMap(currentEnv -> evalAndBind(currentEnv,
                                                                                                                                         binding)));

        if (newEnvEith.isLeft()) {
            return newEnvEith.getLeft();
        }

        return newEnvEith.map(newEnv -> ast.entries()
                                           .get(2)
                                           .eval(newEnv))
                         .get();

    }
}
