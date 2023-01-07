package com.example.mal.types.specials;

import com.example.mal.Singletons;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.coll.MalSequential;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.Tuple2;
import io.vavr.collection.Vector;
import io.vavr.control.Either;
import io.vavr.control.Option;

/**
 * Represents the argument vector of a function.
 * <p>
 * E.g. `(fn* (a b & c) (body ..))` => firstArgs: `[a b]`, restArg: `c`.
 */
@Value.Immutable
@VavrEncodingEnabled
public interface Bindings {

    Vector<MalSymbol> firstArgs();

    Option<MalSymbol> restArg();

    static Either<MalError, Bindings> construct(final MalType input) {
        if (!(input instanceof MalSequential<?> binds)) {
            return Either.left(MalError.of(String.format("Bindings for fn* should be a list, got %s.",
                                                         input.pr())));
        }
        if (binds.entries()
                 .find(e -> !(e instanceof MalSymbol))
                 .isDefined()) {
            return Either.left(MalError.of(String.format("Bindings for fn* should be symbols, got %s.",
                                                         binds.pr())));
        }
        final Vector<MalSymbol> bindings = binds.entries()
                                                .map(e -> (MalSymbol) e)
                                                .toVector();

        if (bindings.find(Singletons.IS_REST)
                    .isDefined()) {
            if (bindings.filter(Singletons.IS_REST)
                        .length() > 1) {
                return Either.left(MalError.of(String.format("Bindings for fn* should contain at most one &-symbol, got %s.",
                                                             binds.pr())));
            }

            if (bindings.indexOf(Singletons.REST) != bindings.length() - 2) {
                return Either.left(MalError.of(String.format("Bindings for fn* may contain the &-symbol as the second to last symbol, got %s.",
                                                             binds.pr())));
            }
        }

        final Tuple2<Vector<MalSymbol>, Vector<MalSymbol>> parts = bindings.splitAt(Singletons.IS_REST);
        return Either.right(ImmutableBindings.builder()
                                             .firstArgs(parts._1())
                                             .restArg(parts._2()
                                                           .drop(1)
                                                           .headOption())
                                             .build());
    }
}
