package com.example.mal.types;

import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;

public interface MalType {

    String pr();

    /**
     * Evaluate a list where `this` is the head (e.g. `(let* ..)`, `(some-fn ..)`)
     * <p>
     * Returns an error by default, as most `MalType`s cannot be invoked
     * (`MalInteger`, `MalString`, ..)
     *
     * @param list The list form to evaluate.
     * @param env  The initial environment
     * @return The new evaluation context
     */
    default EvalContext evalList(final MalList list, final Environment env) {
        return EvalContext.error(String.format("Cannot invoke: '%s'",
                                               pr()));
    }

    /**
     * Evaluate `this` form/atom.
     *
     * By default forms evaluate to themselves.
     *
     * @param env The initial environment
     * @return The new evaluation context
     */
    default EvalContext eval(final Environment env) {
        return EvalContext.withEnv(this,
                                   env);
    }

    /**
     * Iteratively evaluate the target form until a terminal form is produced (TCO).
     *
     * @param target The initial form to evaluate
     * @param env    The initial environment
     * @return The new evaluation context
     */
    static EvalContext evalWithTco(final MalType target, final Environment env) {
        MalType currentForm = target;
        EvalContext currentCtx = null;
        Environment currentEnv = env;
        do {
            currentCtx = currentForm.eval(currentEnv);
            currentForm = currentCtx.result();
            currentEnv = currentCtx.environment()
                                   .getOrElse(currentEnv);
        } while (currentCtx.reEval());

        return currentCtx;
    }
}
