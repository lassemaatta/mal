package com.example.mal.env;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import com.example.mal.types.MalError;
import com.example.mal.types.MalInteger;
import com.example.mal.types.MalList;
import com.example.mal.types.MalType;

public class Functions {

    public static MalType plus(final MalList args) {
        // (+) => 0
        if (args.entries()
                .isEmpty()) {
            return MalInteger.of(new BigDecimal(0));
        }
        return calculate(args,
                         MalInteger::add);
    }

    public static MalType minus(final MalList args) {
        final int count = args.entries()
                              .length();
        if (count == 0) {
            return MalError.of("Wrong number of arguments (0)");
        }
        // (- N) => -N
        if (count == 1) {
            if (args.entries()
                    .head() instanceof MalInteger i) {
                return MalInteger.negate(i);
            }
            return MalError.of("Not an integer");
        }
        return calculate(args,
                         MalInteger::subtract);
    }

    public static MalType multiply(final MalList args) {
        // (*) => 1
        if (args.entries()
                .isEmpty()) {
            return MalInteger.of(new BigDecimal(1));
        }
        return calculate(args,
                         MalInteger::multiply);
    }

    public static MalType divide(final MalList args) {
        if (args.entries()
                .length() < 2) {
            return MalError.of("Wrong number of arguments (0)");
        }
        return calculate(args,
                         MalInteger::divide);
    }

    private static MalType calculate(final MalList args,
                                     final BiFunction<MalInteger, MalInteger, MalInteger> f) {
        if (args.entries()
                .find(entry -> !(entry instanceof MalInteger))
                .isDefined()) {
            return MalError.of("Not an integer");
        }
        return args.entries()
                   .map(entry -> (MalInteger) entry)
                   .reduce(f);
    }
}
