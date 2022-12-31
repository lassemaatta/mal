package com.example.mal.types;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.env.Environment;
import com.google.common.base.Joiner;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.Seq;
import io.vavr.control.Option;

public abstract class MalCollection<T extends Seq<MalType>> implements MalType {
    protected abstract T entries();

    interface Builder {
        Builder addEntries(MalType type);

        Builder addAllEntries(Iterable<MalType> entries);

        MalType build();
    }

    protected String pr(final Character startToken, final Character endToken) {
        return startToken + Joiner.on(" ")
                                  .join(entries().map(MalType::pr))
                + endToken;
    }

    protected MalType eval(final Environment env, final MalCollection.Builder builder) {
        if (entries().isEmpty()) {
            return this;
        }
        final Seq<MalType> evaled = entries().map(e -> e.eval(env))
                                             .map(e -> (MalType) e);

        final Option<MalType> error = evaled.find(e -> e instanceof MalError);

        if (error.isDefined()) {
            return error.get();
        }

        return builder.addAllEntries(evaled)
                      .build();
    }

    public static Tuple2<Reader, MalType> read(Reader r,
                                               final MalCollection.Builder builder,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader,
                                               final Character endToken) {

        // consume start-of-coll
        r = r.next();

        while (true) {
            if (r.peek()
                 .isEmpty()) {
                return Tuple.of(r,
                                MalError.of("EOF while reading list"));
            }

            final String token = r.peek()
                                  .get();

            if (endToken.equals(token.charAt(0)) && token.length() == 1) {
                break;
            }

            final Tuple2<Reader, MalType> res = formReader.apply(r);

            if (res._2() instanceof MalError) {
                return res;
            }

            r = res._1();
            builder.addEntries(res._2());
        }

        // Consume end-of-coll
        r = r.next();

        return Tuple.of(r,
                        builder.build());
    }
}
