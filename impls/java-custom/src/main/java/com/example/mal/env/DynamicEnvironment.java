package com.example.mal.env;

import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;

@Value.Immutable
@VavrEncodingEnabled
public abstract class DynamicEnvironment implements Environment {

    protected abstract HashMap<MalSymbol, MalType> values();

    protected abstract HashMap<MalSymbol, MalType> locals();

    @Override
    public Option<MalType> lookupValue(final MalSymbol symbol) {
        return locals().get(symbol)
                       .orElse(values().get(symbol));
    }

    @Override
    public Environment set(final MalSymbol symbol, final MalType value) {
        return ImmutableDynamicEnvironment.copyOf(this)
                                          .withValues(values().put(symbol,
                                                                   value));
    }

    @Override
    public Environment setLocal(final MalSymbol symbol, final MalType value) {
        return ImmutableDynamicEnvironment.copyOf(this)
                                          .withLocals(locals().put(symbol,
                                                                   value));
    }

    @Override
    public Environment clearLocals() {
        return ImmutableDynamicEnvironment.copyOf(this)
                                          .withLocals(HashMap.empty());
    }

    public static Environment empty() {
        return ImmutableDynamicEnvironment.builder()
                                          .values(HashMap.empty())
                                          .locals(HashMap.empty())
                                          .build();
    }
}
