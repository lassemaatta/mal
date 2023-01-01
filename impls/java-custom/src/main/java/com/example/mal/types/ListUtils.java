package com.example.mal.types;

import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;

import org.immutables.value.Value;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;

public class ListUtils {

    @Value.Immutable
    public interface ListEvalCtx {

        /**
         * @return The current environment
         */
        Environment environment();

        /**
         * @return Accumulator for the evaluated arguments
         */
        List<MalType> arguments();

        static ListEvalCtx empty(final Environment initial) {
            return ImmutableListEvalCtx.builder()
                                       .environment(initial)
                                       .arguments(List.empty())
                                       .build();
        }

        default ListEvalCtx with(final Option<Environment> env, final MalType argument) {
            return ImmutableListEvalCtx.copyOf(this)
                                       .withEnvironment(env.getOrElse(environment()))
                                       .withArguments(arguments().append(argument));
        }
    }

    private static Either<MalError, ListEvalCtx> evalEntry(final ListEvalCtx ctx, final MalType entry) {
        final EvalContext newCtx = MalType.evalWithTco(entry,
                                                       ctx.environment());
        final MalType val = newCtx.result();
        if (val instanceof MalError err) {
            return Either.left(err);
        }
        return Either.right(ctx.with(newCtx.environment(),
                                     val));
    }

    /**
     * Evaluate each given form while passing (and potentially updating) the current
     * environment
     *
     * @param initialEnv The initial environment
     * @param entries    The forms to evaluate
     * @return List of evaluated forms and the resulting environment, or an error
     *         form
     */
    public static Either<MalError, ListEvalCtx> evalEach(final Environment initialEnv, final List<MalType> entries) {
        return entries.foldLeft(Either.right(ListEvalCtx.empty(initialEnv)),
                                (currentCtxEither,
                                 entry) -> currentCtxEither.flatMap(currentCtx -> evalEntry(currentCtx,
                                                                                            entry)));
    }
}
