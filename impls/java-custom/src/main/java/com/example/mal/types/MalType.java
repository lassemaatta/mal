package com.example.mal.types;

import com.example.mal.env.Environment;

public interface MalType {
    String pr();

    default MalType eval(final Environment env) {
        return this;
    }
}
