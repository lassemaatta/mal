package com.example.mal.env;

import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;

import io.vavr.control.Option;

public interface Environment {
    Option<MalType> lookupValue(MalSymbol symbol);

    Environment set(MalSymbol symbol, MalType value);
}
