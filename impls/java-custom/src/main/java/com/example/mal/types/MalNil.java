package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;

import org.immutables.value.Value;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalNil implements MalType {

    private static final String NIL = "nil";

    @Override
    public String pr() {
        return NIL;
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> NIL.equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static MalNil nil() {
        return ImmutableMalNil.builder()
                              .build();
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.NIL))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read nil")));
    }
}
