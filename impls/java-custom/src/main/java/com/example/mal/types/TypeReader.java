package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;

import io.vavr.Tuple2;

@FunctionalInterface
public interface TypeReader {
    Tuple2<Reader, MalType> apply(Reader r, Function<Reader, Tuple2<Reader, MalType>> formReader);
}
