package com.example.mal.types;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

@Value.Immutable
public interface MalContinue extends MalType {

    @Override
    @Lazy
    default String pr() {
        throw new IllegalStateException("Cannot pr a MalContinue!");
    }
}
