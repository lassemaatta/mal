package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;

import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public abstract class MalReaderMacro implements MalType {

    @Override
    @Lazy
    public String pr() {
        throw new IllegalStateException();
    }

    public static boolean matches(final Reader r, final String symbol) {
        return r.peek()
                .map(token -> symbol.equalsIgnoreCase(token))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader,
                                               final MalSymbol head) {
        return r.peek()
                .map(token -> {
                    final var children = formReader.apply(r.next());
                    if (children._2() instanceof MalError) {
                        return children;
                    }
                    return Tuple.of(children._1(),
                                    (MalType) MalList.of(head,
                                                         children._2()));
                })
                .getOrElse(Tuple.of(r,
                                    MalError.of(String.format("Failed to read '%s'",
                                                              head.name()))));
    }
}
