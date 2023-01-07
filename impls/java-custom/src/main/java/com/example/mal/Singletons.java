
package com.example.mal;

import java.util.function.Predicate;

import com.example.mal.types.ImmutableMalContinue;
import com.example.mal.types.MalContinue;
import com.example.mal.types.atoms.MalBoolean;
import com.example.mal.types.atoms.MalNil;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.specials.MalDefBang;
import com.example.mal.types.specials.MalFnStar;
import com.example.mal.types.specials.MalLetStar;

public class Singletons {

    public static final MalContinue CONTINUE = ImmutableMalContinue.builder()
                                                                   .build();

    public static final MalNil NIL = MalNil.nil();

    public static final MalBoolean TRUE = MalBoolean.of(true);
    public static final MalBoolean FALSE = MalBoolean.of(false);

    public static final MalSymbol QUOTE = MalSymbol.of("quote");
    public static final MalSymbol UNQUOTE = MalSymbol.of("unquote");
    public static final MalSymbol QUASI_QUOTE = MalSymbol.of("quasiquote");
    public static final MalSymbol SPLICE_UNQUOTE = MalSymbol.of("splice-unquote");
    public static final MalSymbol DEREF = MalSymbol.of("deref");
    public static final MalSymbol WITH_META = MalSymbol.of("with-meta");

    public static final MalSymbol REST = MalSymbol.of("&");

    public static final Predicate<MalSymbol> IS_REST = s -> Singletons.REST.equals(s);

    public static final MalSymbol PLUS = MalSymbol.of("+");
    public static final MalSymbol MINUS = MalSymbol.of("-");
    public static final MalSymbol MULTIPLY = MalSymbol.of("*");
    public static final MalSymbol DIVIDE = MalSymbol.of("/");

    public static final MalDefBang DEF_BANG = MalDefBang.instance();
    public static final MalLetStar LET_STAR = MalLetStar.instance();
    public static final MalFnStar FN_STAR = MalFnStar.instance();
}
