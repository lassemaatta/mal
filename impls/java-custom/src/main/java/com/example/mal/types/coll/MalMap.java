package com.example.mal.types.coll;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Vector;

@Value.Immutable
@VavrEncodingEnabled
public abstract class MalMap extends MalCollection<Vector<MalType>> {

    private static final Character START_TOKEN = '{';
    private static final Character END_TOKEN = '}';

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
    public EvalContext eval(final Environment env) {
        return super.eval(env,
                          ImmutableMalMap.builder());
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> START_TOKEN.equals(token.charAt(0)))
                .getOrElse(false);
    }

    public static Tuple2<Reader, MalType> read(final Reader r, Function<Reader, Tuple2<Reader, MalType>> formReader) {
        final var res = MalCollection.read(r,
                                           ImmutableMalMap.builder(),
                                           formReader,
                                           END_TOKEN);
        final MalMap map = (MalMap) res._2();

        if (map.entries()
               .size() % 2 != 0) {
            return Tuple.of(r,
                            MalError.of("Map requires even number of elements!"));
        }

        return res;
    }
}
