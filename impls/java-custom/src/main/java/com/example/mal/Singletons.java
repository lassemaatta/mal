
package com.example.mal;

import com.example.mal.types.ImmutableMalContinue;
import com.example.mal.types.MalBoolean;
import com.example.mal.types.MalContinue;
import com.example.mal.types.MalDefBang;
import com.example.mal.types.MalLetStar;
import com.example.mal.types.MalNil;
import com.example.mal.types.MalSymbol;

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

    public static final MalSymbol PLUS = MalSymbol.of("+");
    public static final MalSymbol MINUS = MalSymbol.of("-");
    public static final MalSymbol MULTIPLY = MalSymbol.of("*");
    public static final MalSymbol DIVIDE = MalSymbol.of("/");

    public static final MalDefBang DEF_BANG = MalDefBang.instance();
    public static final MalLetStar LET_STAR = MalLetStar.instance();
}
