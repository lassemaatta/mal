package com.example.mal.types;

import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.coll.ListUtils;
import com.example.mal.types.coll.ListUtils.ListEvalCtx;
import com.example.mal.types.coll.MalList;
import com.example.mal.types.specials.Bindings;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.List;
import io.vavr.collection.Vector;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * A function, whose body is implemented in MAL
 */
@Value.Immutable
@VavrEncodingEnabled
public abstract class MalRuntimeFn extends MalFn {

    /**
     * @return The symbols representing to the argument vector
     */
    public abstract Bindings binds();

    public abstract MalType body();

    public static MalFn of(final Bindings binds, final MalType body) {
        return ImmutableMalRuntimeFn.builder()
                                    .binds(binds)
                                    .body(body)
                                    .build();
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {

        // System.out.println(String.format("Runtime: %s",
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

        // It would make sense to clear the locals from `newEnv` so that
        // the function environment only contains the global symbols and
        // the arguments. However, MAL seems to favor dynamic binding instead
        // of lexical binding (e.g. `(let* (f (fn* () x) x 3) (f))`) so
        // let's not clear the locals here

        final Vector<MalSymbol> first = binds().firstArgs();
        final Option<MalSymbol> rest = binds().restArg();

        final int expected = first.length();
        final int actual = args.length();
        if (rest.isEmpty()) {
            if (expected != actual) {
                return EvalContext.error(String.format("Arity error. Expected %s arguments, got %s.",
                                                       expected,
                                                       actual));
            }
        } else {
            if (expected > actual) {
                return EvalContext.error(String.format("Arity error. Expected at least %s arguments, got %s.",
                                                       expected,
                                                       actual));
            }
        }

        // Bind each argument symbol to the corresponding value (excl. the & rest arg)
        final Environment e1 = first.zip(args)
                                    .foldLeft(newEnv,
                                              (e, t) -> {
                                                  return e.setLocal(t._1(),
                                                                    t._2());
                                              });
        // Bind the rest of the args to the rest symbol
        final Environment e2 = rest.map(restSym -> e1.setLocal(restSym,
                                                               MalList.ofIterable(args.drop(first.length()))))
                                   .getOrElse(e1);

        // Evaluate the actual function body with TCO in an environment,
        // where the argument symbols are bound to the argument values
        return EvalContext.reEval(body(),
                                  e2);
    }
}
