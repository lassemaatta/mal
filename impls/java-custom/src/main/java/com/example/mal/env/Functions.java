package com.example.mal.env;

import java.io.PrintWriter;
import java.math.BigDecimal;
import java.util.function.BiFunction;

import com.example.mal.Singletons;
import com.example.mal.types.MalError;
import com.example.mal.types.MalNativeFn;
import com.example.mal.types.MalType;
import com.example.mal.types.atoms.MalBoolean;
import com.example.mal.types.atoms.MalInteger;
import com.example.mal.types.coll.MalCollection;
import com.example.mal.types.coll.MalList;

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

    public static final MalType LIST = MalNativeFn.of("list",
                                                      args -> MalList.ofIterable(args));

    public static final MalType LIST_QMARK = MalNativeFn.of("list?",
                                                            args -> args.headOption()
                                                                        .map(head -> head instanceof MalList)
                                                                        .getOrElse(false) ? Singletons.TRUE
                                                                                : Singletons.FALSE);

    public static final MalType EMPTY_QMARK = MalNativeFn.of("empty?",
                                                             args -> args.headOption()
                                                                         .filter(head -> head instanceof MalCollection)
                                                                         .map(head -> (MalCollection<?>) head)
                                                                         .map(list -> list.entries()
                                                                                          .isEmpty())
                                                                         .map(empty -> (MalType) MalBoolean.of(empty))
                                                                         .getOrElse(MalError.of("empty? supports only collections")));

    public static final MalType COUNT = MalNativeFn.of("count",
                                                       args -> args.headOption()
                                                                   .filter(head -> head instanceof MalCollection)
                                                                   .map(head -> (MalCollection<?>) head)
                                                                   .map(list -> list.entries()
                                                                                    .length())
                                                                   .map(count -> (MalType) MalInteger.of(BigDecimal.valueOf(count)))
                                                                   .getOrElse(MalInteger.of(BigDecimal.ZERO)));

    public static MalType makePrn(final PrintWriter writer) {
        return MalNativeFn.of("prn",
                              args -> {
                                  args.headOption()
                                      .map(head -> head.pr())
                                      .forEach(str -> writer.write(str));
                                  return Singletons.NIL;
                              });
    }

    public static final MalType EQ = MalNativeFn.of("=",
                                                    args -> MalBoolean.of(args.sliding(2,
                                                                                       1)
                                                                              .map(pair -> pair.get(0)
                                                                                               .equals(pair.get(1)))
                                                                              .foldLeft(true,
                                                                                        (acc, eq) -> acc && eq)));

}
