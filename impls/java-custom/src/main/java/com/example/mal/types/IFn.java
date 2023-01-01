package com.example.mal.types;

import io.vavr.collection.List;

@FunctionalInterface
public interface IFn {
    MalType apply(List<MalType> args);
}
