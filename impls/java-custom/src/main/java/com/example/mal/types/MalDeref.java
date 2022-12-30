package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;

import org.immutables.value.Value;

import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalDeref extends MalReaderMacro {

    private static final String DEREF = "@";

    public static boolean matches(final Reader r) {
        return MalReaderMacro.matches(r,
                                      DEREF);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return MalReaderMacro.read(r,
                                   formReader,
                                   Singletons.DEREF);
    }
}
