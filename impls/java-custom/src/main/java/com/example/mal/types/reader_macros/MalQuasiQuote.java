package com.example.mal.types.reader_macros;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.types.MalType;

import org.immutables.value.Value;

import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalQuasiQuote extends MalReaderMacro {

    private static final String QUASI_QUOTE = "`";

    public static boolean matches(final Reader r) {
        return MalReaderMacro.matches(r,
                                      QUASI_QUOTE);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return MalReaderMacro.read(r,
                                   formReader,
                                   Singletons.QUASI_QUOTE);
    }
}
