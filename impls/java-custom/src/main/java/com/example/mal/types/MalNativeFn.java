package com.example.mal.types;

import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.coll.ListUtils;
import com.example.mal.types.coll.ListUtils.ListEvalCtx;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.List;
import io.vavr.control.Either;

/**
 * A function, whose body is implemented as a native Java function.
 */
@Value.Immutable
@VavrEncodingEnabled
public abstract class MalNativeFn extends MalFn {

    public abstract IFn body();

    public static MalFn of(final String name, final IFn body) {
        return ImmutableMalNativeFn.builder()
                                   .name(name)
                                   .body(body)
                                   .build();
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {

        // System.out.println(String.format("Native: %s",
        // ast.pr()));

        // System.out.println(env);

        // Evaluate the function arguments
        final Either<MalError, ListEvalCtx> res = ListUtils.evalEach(env,
                                                                     ast.entries()
                                                                        .pop());

        // Evaluating the arguments caused an error
        if (res.isLeft()) {
            return EvalContext.done(res.getLeft());
        }
        final List<MalType> args = res.get()
                                      .arguments();
        final Environment newEnv = res.get()
                                      .environment();
        return EvalContext.withEnv(body().apply(args),
                                   newEnv);
    }
}
