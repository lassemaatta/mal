package com.example.mal.types;

import java.util.function.Function;
import java.util.regex.Pattern;

import com.example.mal.Reader;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalSymbol implements MalType {

    private static final Pattern PATTERN =
            Pattern.compile("[\\p{Alpha}\\*\\+\\!\\-_'\\?\\<\\>=/]+[\\p{Alnum}\\*\\+\\!\\-_'\\?\\<\\>=/]*");

    public abstract String name();

    @Override
    @Lazy
    public String pr() {
        return name();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> PATTERN.matcher(token)
                                     .matches())
                .getOrElse(false);
    }

    public static MalSymbol of(final String token) {
        return ImmutableMalSymbol.builder()
                                 .name(token)
                                 .build();
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) of(token)))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read symbol")));
    }
}
