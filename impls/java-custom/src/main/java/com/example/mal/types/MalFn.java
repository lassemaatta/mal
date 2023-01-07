package com.example.mal.types;

import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.coll.ListUtils;
import com.example.mal.types.coll.ListUtils.ListEvalCtx;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.List;
import io.vavr.control.Either;
import io.vavr.control.Option;

@Value.Immutable
@VavrEncodingEnabled
public abstract class MalFn implements MalType, IFn {

    public abstract Option<String> name();

    public abstract IFn body();

    @Override
    public MalType apply(final List<MalType> args) {
        return body().apply(args);
    }

    @Override
    @Lazy
    public String pr() {
        return String.format("Function<%s>",
                             name().getOrElse("anonymous"));
    }

    public static MalFn of(final String name, final IFn body) {
        return ImmutableMalFn.builder()
                             .name(name)
                             .body(body)
                             .build();
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() == 0) {
            return EvalContext.error("Cannot invoke empty list");
        }

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
        return EvalContext.withEnv(apply(args),
                                   newEnv);
    }
}
