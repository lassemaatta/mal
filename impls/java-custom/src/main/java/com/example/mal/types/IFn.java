package com.example.mal.types;

@FunctionalInterface
public interface IFn {
    MalType apply(MalList args);
}
