package com.example.mal.types;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

@Value.Immutable
public interface MalError extends MalType {

    String message();

    @Override
    @Lazy
    default String pr() {
        throw new IllegalStateException();
    }

    static MalError of(final String message) {
        return ImmutableMalError.builder()
                                .message(message)
                                .build();
    }
}
