package com.example.mal.types.atoms;

import java.math.BigDecimal;
import java.util.function.Function;
import java.util.regex.Pattern;

import com.example.mal.Reader;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalInteger implements MalType {

    private static final Pattern PATTERN = Pattern.compile("[-]?[\\d]+");

    public abstract BigDecimal number();

    @Override
    @Lazy
    public String pr() {
        return number().toString();
    }

    public MalInteger add(final MalInteger other) {
        return of(number().add(other.number()));
    }

    public MalInteger subtract(final MalInteger other) {
        return of(number().subtract(other.number()));
    }

    public MalInteger multiply(final MalInteger other) {
        return of(number().multiply(other.number()));
    }

    public MalInteger divide(final MalInteger other) {
        return of(number().divide(other.number()));
    }

    public static MalInteger negate(final MalInteger i) {
        return of(i.number()
                   .negate());
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> PATTERN.matcher(token)
                                     .matches())
                .getOrElse(false);
    }

    public static MalInteger of(final BigDecimal value) {
        return ImmutableMalInteger.builder()
                                  .number(value)
                                  .build();
    }

    private static MalType read(final String token) {
        try {
            return of(new BigDecimal(token));
        } catch (NumberFormatException e) {
            return MalError.of(String.format("Failed to parse integer: '%s'",
                                             token));
        }
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       read(token)))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read integer")));
    }
}
