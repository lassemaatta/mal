package com.example.mal.env;

import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;

import io.vavr.control.Option;

public interface Environment {
    Option<MalType> lookupValue(MalSymbol symbol);

    /**
     * Create a new `Environment`, with the non-local `symbol` mapped to the
     * `value`.
     *
     * @param symbol The symbol to use as the key
     * @param value  The value corresponding to the symbol
     * @return A new environment, which includes the new mapping
     */
    Environment set(MalSymbol symbol, MalType value);

    Environment setLocal(MalSymbol symbol, MalType value);

    Environment clearLocals();
}
