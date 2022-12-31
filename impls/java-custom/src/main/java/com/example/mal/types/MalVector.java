package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.env.Environment;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.Tuple2;
import io.vavr.collection.Vector;

@Value.Immutable
@VavrEncodingEnabled
public abstract class MalVector extends MalCollection<Vector<MalType>> {

    private static final Character START_TOKEN = '[';
    private static final Character END_TOKEN = ']';

    public abstract Vector<MalType> entries();

    interface Builder extends MalCollection.Builder {
    }

    @Override
    @Lazy
    public String pr() {
        return super.pr(START_TOKEN,
                        END_TOKEN);
    }

    @Override
    public MalType eval(final Environment env) {
        return super.eval(env, ImmutableMalVector.builder());
    }


    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> START_TOKEN.equals(token.charAt(0)))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return MalCollection.read(r,
                                  ImmutableMalVector.builder(),
                                  formReader,
                                  END_TOKEN);
    }
}
