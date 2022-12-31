package com.example.mal.types;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.control.Option;

@Value.Immutable
@VavrEncodingEnabled
public abstract class MalFn implements MalType, IFn {

    public abstract Option<String> name();

    public abstract IFn body();

    @Override
    public MalType apply(final MalList args) {
        return body().apply(args);
    }

    @Override
    @Lazy
    public String pr() {
        return String.format("Function<%s>",
                             name().getOrElse("anonymous"));
    }

    public static MalFn of(final String name, final IFn body) {
        return ImmutableMalFn.builder()
                             .name(name)
                             .body(body)
                             .build();
    }

}
