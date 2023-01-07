package com.example.mal.env;

import java.math.BigDecimal;
import java.util.function.BiFunction;

import com.example.mal.types.MalNativeFn;
import com.example.mal.types.MalError;
import com.example.mal.types.MalFn;
import com.example.mal.types.MalType;
import com.example.mal.types.atoms.MalInteger;

import io.vavr.collection.List;

public class Functions {

    public static final MalType PLUS = MalNativeFn.of("+",
                                                      args -> {
                                                          // (+) => 0
                                                          if (args.isEmpty()) {
                                                              return MalInteger.of(new BigDecimal(0));
                                                          }
                                                          return calculate(args,
                                                                           MalInteger::add);
                                                      });

    public static final MalType MINUS = MalNativeFn.of("-",
                                                       args -> {
                                                           final int count = args.length();
                                                           if (count == 0) {
                                                               return MalError.of("Wrong number of arguments (0)");
                                                           }
                                                           // (- N) => -N
                                                           if (count == 1) {
                                                               if (args.head() instanceof MalInteger i) {
                                                                   return MalInteger.negate(i);
                                                               }
                                                               return MalError.of("Not an integer");
                                                           }
                                                           return calculate(args,
                                                                            MalInteger::subtract);
                                                       });

    public static final MalType MULTIPLY = MalNativeFn.of("*",
                                                          args -> {
                                                              // (*) => 1
                                                              if (args.isEmpty()) {
                                                                  return MalInteger.of(new BigDecimal(1));
                                                              }
                                                              return calculate(args,
                                                                               MalInteger::multiply);
                                                          });

    public static final MalType DIVIDE = MalNativeFn.of("/",
                                                        args -> {
                                                            if (args.length() < 2) {
                                                                return MalError.of("Wrong number of arguments (0)");
                                                            }
                                                            return calculate(args,
                                                                             MalInteger::divide);
                                                        });

    private static MalType calculate(final List<MalType> args, final BiFunction<MalInteger, MalInteger, MalInteger> f) {
        if (args.find(entry -> !(entry instanceof MalInteger))
                .isDefined()) {
            return MalError.of("Not an integer");
        }
        return args.map(entry -> (MalInteger) entry)
                   .reduce(f);
    }
}
