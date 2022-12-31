package com.example.mal.types;

import com.example.mal.env.Environment;

import io.vavr.Tuple;
import io.vavr.Tuple2;

public interface MalType {
    String pr();

    default MalType eval(final Environment env) {
        return this;
    }

    default Tuple2<Environment, MalType> rootEval(final Environment env) {
        return Tuple.of(env, eval(env));
    }
}
