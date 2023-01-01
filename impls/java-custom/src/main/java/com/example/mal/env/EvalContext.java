package com.example.mal.env;

import com.example.mal.types.MalError;
import com.example.mal.types.MalType;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.control.Option;

@Value.Immutable
@VavrEncodingEnabled
public interface EvalContext {

    MalType result();

    boolean reEval();

    Option<Environment> environment();

    /**
     * Indicate that the callee should re-evaluate with the given context (TCO).
     *
     * @param result The form to evaluate
     * @param env    The environment to use for eval
     */
    static EvalContext reEval(final MalType result, final Environment env) {
        return ImmutableEvalContext.builder()
                                   .reEval(true)
                                   .result(result)
                                   .environment(env)
                                   .build();
    }

    static EvalContext withEnv(final MalType result, final Environment env) {
        return ImmutableEvalContext.builder()
                                   .reEval(false)
                                   .result(result)
                                   .environment(env)
                                   .build();
    }

    static EvalContext done(final MalType result) {
        return ImmutableEvalContext.builder()
                                   .reEval(false)
                                   .result(result)
                                   .build();
    }

    static EvalContext error(final String error) {
        return done(MalError.of(error));
    }
}
