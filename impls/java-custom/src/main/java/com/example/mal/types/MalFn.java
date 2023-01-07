package com.example.mal.types;

import org.immutables.value.Value.Lazy;

import io.vavr.control.Option;

public abstract class MalFn implements MalType {

    public abstract Option<String> name();

    @Override
    @Lazy
    public String pr() {
        return String.format("#<%s>",
                             name().getOrElse("function"));
    }
}
