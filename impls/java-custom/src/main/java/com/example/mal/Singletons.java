
package com.example.mal;

import java.util.function.Predicate;

import com.example.mal.types.ImmutableMalContinue;
import com.example.mal.types.MalContinue;
import com.example.mal.types.atoms.MalBoolean;
import com.example.mal.types.atoms.MalNil;
import com.example.mal.types.atoms.MalSymbol;
import com.example.mal.types.specials.MalDefBang;
import com.example.mal.types.specials.MalDo;
import com.example.mal.types.specials.MalFnStar;
import com.example.mal.types.specials.MalIf;
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
    public static final MalSymbol PRN = MalSymbol.of("prn");
    public static final MalSymbol PRINTLN = MalSymbol.of("println");
    public static final MalSymbol STR = MalSymbol.of("str");
    public static final MalSymbol LIST = MalSymbol.of("list");
    public static final MalSymbol LIST_QMARK = MalSymbol.of("list?");
    public static final MalSymbol EMPTY_QMARK = MalSymbol.of("empty?");
    public static final MalSymbol COUNT = MalSymbol.of("count");
    public static final MalSymbol EQ = MalSymbol.of("=");
    public static final MalSymbol LT = MalSymbol.of("<");
    public static final MalSymbol LT_EQ = MalSymbol.of("<=");
    public static final MalSymbol GT = MalSymbol.of(">");
    public static final MalSymbol GT_EQ = MalSymbol.of(">=");

    public static final MalDefBang DEF_BANG = MalDefBang.instance();
    public static final MalLetStar LET_STAR = MalLetStar.instance();
    public static final MalFnStar FN_STAR = MalFnStar.instance();
    public static final MalDo DO = MalDo.instance();
    public static final MalIf IF = MalIf.instance();
}
