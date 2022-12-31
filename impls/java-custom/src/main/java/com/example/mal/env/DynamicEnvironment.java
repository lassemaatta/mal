package com.example.mal.env;

import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;

import org.immutables.value.Value;

import io.vavr.collection.HashMap;
import io.vavr.control.Option;

@Value.Immutable
public abstract class DynamicEnvironment implements Environment {

    protected abstract HashMap<MalSymbol, MalType> values();

    @Override
    public Option<MalType> lookupValue(final MalSymbol symbol) {
        return values().get(symbol);
    }

    @Override
    public Environment set(final MalSymbol symbol, final MalType value) {
        return ImmutableDynamicEnvironment.copyOf(this)
                                          .withValues(values().put(symbol,
                                                                   value));
    }

    public static Environment empty() {
        return ImmutableDynamicEnvironment.builder()
                                          .values(HashMap.empty())
                                          .build();
    }
}
