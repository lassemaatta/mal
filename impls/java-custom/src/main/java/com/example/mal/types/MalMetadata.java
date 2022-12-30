package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;

import org.immutables.value.Value;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalMetadata extends MalReaderMacro {

    private static final String METADATA = "^";

    public static boolean matches(final Reader r) {
        return MalReaderMacro.matches(r,
                                      METADATA);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> {
                    final var children_metadata = formReader.apply(r.next());
                    if (children_metadata._2() instanceof MalError) {
                        return children_metadata;
                    }
                    final var children_target = formReader.apply(children_metadata._1());
                    if (children_target._2() instanceof MalError) {
                        return children_target;
                    }

                    return Tuple.of(children_target._1(),
                                    (MalType) MalList.of(Singletons.WITH_META,
                                                         children_target._2(),
                                                         children_metadata._2()));
                })
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read metadata")));
    }
}
