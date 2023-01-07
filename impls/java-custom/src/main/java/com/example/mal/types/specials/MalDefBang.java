package com.example.mal.types.specials;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.Singletons;
import com.example.mal.env.Environment;
import com.example.mal.env.EvalContext;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.coll.MalList;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;

@Value.Immutable
public abstract class MalDefBang implements MalType {

    @Override
    @Lazy
    public String pr() {
        return "def!";
    }

    public static MalDefBang instance() {
        return ImmutableMalDefBang.builder()
                                  .build();
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> "def!".equalsIgnoreCase(token))
                .getOrElse(false);
    }

    @Override
    public EvalContext eval(final Environment env) {
        return EvalContext.error("def! is valid only as the first element of a top-level form!");
    }

    @Override
    public EvalContext evalList(final MalList ast, final Environment env) {
        if (ast.entries()
               .length() != 3) {
            return EvalContext.error(String.format("def! should have 3 elements, got %s",
                                                   ast.entries()
                                                      .length()));
        }
        // Second form should be a symbol
        final MalType second = ast.entries()
                                  .get(1);
        if (!(second instanceof MalSymbol s)) {
            return EvalContext.error(String.format("Second element of def! should be a, got '%s'",
                                                   second.pr()));
        }
        // The third form is evaluated to build the value
        final EvalContext ctx = MalType.evalWithTco(ast.entries()
                                                       .get(2),
                                                    env);
        final MalType body = ctx.result();
        if (body instanceof MalError err) {
            return ctx;
        }

        return EvalContext.withEnv(body,
                                   ctx.environment()
                                      .getOrElse(env)
                                      .set(s,
                                           body));
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .map(token -> Tuple.of(r.next(),
                                       (MalType) Singletons.DEF_BANG))
                .getOrElse(Tuple.of(r,
                                    MalError.of("Failed to read def!")));
    }
}
