package com.example.mal.env;

import com.example.mal.Singletons;
import com.example.mal.types.MalFn;
import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;

public class RootEnv implements Environment {
    private static final HashMap<MalSymbol, MalType> ENV = HashMap.of(Singletons.PLUS,
                                                                      MalFn.of("+",
                                                                               Functions::plus),
                                                                      Singletons.MINUS,
                                                                      MalFn.of("-",
                                                                               Functions::minus),
                                                                      Singletons.MULTIPLY,
                                                                      MalFn.of("*",
                                                                               Functions::multiply),
                                                                      Singletons.DIVIDE,
                                                                      MalFn.of("/",
                                                                               Functions::divide));

    public static final RootEnv ROOT_ENV = new RootEnv();

    @Override
    public Option<MalType> lookupValue(final MalSymbol symbol) {
        return ENV.get(symbol);
    }

    @Override
    public Environment set(MalSymbol symbol, MalType value) {
        throw new IllegalStateException();
    }

    @Override
    public Environment setLocal(final MalSymbol symbol, final MalType value) {
        throw new IllegalStateException();
    }

    @Override
    public Environment clearLocals() {
        throw new IllegalStateException();
    }
}
