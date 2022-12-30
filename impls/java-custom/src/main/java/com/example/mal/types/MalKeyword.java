package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalKeyword implements MalType {
    public abstract String name();

    @Override
    @Lazy
    public String pr() {
        return ":" + name();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> token.startsWith(":"))
                .getOrElse(false);
    }

    public static MalKeyword of(final String token) {
        return ImmutableMalKeyword.builder()
                                  .name(token)
                                  .build();
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) of(token.substring(1))))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read keyword")));
    }
}
