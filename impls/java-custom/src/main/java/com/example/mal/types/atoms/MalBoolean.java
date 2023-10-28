package com.example.mal.types.atoms;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.immutables.value.Value;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalBoolean implements MalType {

    private static final String TRUE = "true";
    private static final String FALSE = "false";

    public abstract boolean value();

    @Override
    public String pr() {
        return value() ? TRUE : FALSE;
    }

    @Override
    public boolean isTruthy() {
        return value();
    }

    public MalBoolean and(final MalBoolean other) {
        return value() && other.value() ? Singletons.TRUE : Singletons.FALSE;
    }

    public MalBoolean or(final MalBoolean other) {
        return value() || other.value() ? Singletons.TRUE : Singletons.FALSE;
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> TRUE.equalsIgnoreCase(token) || FALSE.equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static MalBoolean of(final boolean value) {
        return ImmutableMalBoolean.builder()
                                  .value(value)
                                  .build();
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       TRUE.equalsIgnoreCase(token) ? (MalType) Singletons.TRUE : Singletons.FALSE))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read boolean")));
    }
}
