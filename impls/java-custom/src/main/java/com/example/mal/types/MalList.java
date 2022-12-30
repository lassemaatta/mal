package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.Tuple2;
import io.vavr.collection.List;

@Value.Immutable
@VavrEncodingEnabled
public abstract class MalList extends MalCollection<List<MalType>> {

    private static final Character START_TOKEN = '(';
    private static final Character END_TOKEN = ')';

    public abstract List<MalType> entries();

    interface Builder extends MalCollection.Builder {
    }

    @Override
    @Lazy
    public String pr() {
        return super.pr(START_TOKEN,
                        END_TOKEN);
    }

    public static MalList of(final MalType... entries) {
        return ImmutableMalList.builder()
                               .addEntries(entries)
                               .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> START_TOKEN.equals(token.charAt(0)))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        // System.out.println("Reading list");
        return MalCollection.read(r,
                                  ImmutableMalList.builder(),
                                  formReader,
                                  END_TOKEN);
    }
}
