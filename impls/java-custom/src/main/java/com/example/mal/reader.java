package com.example.mal;

import java.util.function.Function;

import com.example.mal.types.MalBoolean;
import com.example.mal.types.MalDefBang;
import com.example.mal.types.MalDeref;
import com.example.mal.types.MalError;
import com.example.mal.types.MalInteger;
import com.example.mal.types.MalKeyword;
import com.example.mal.types.MalLetStar;
import com.example.mal.types.MalList;
import com.example.mal.types.MalMap;
import com.example.mal.types.MalMetadata;
import com.example.mal.types.MalNil;
import com.example.mal.types.MalQuasiQuote;
import com.example.mal.types.MalQuote;
import com.example.mal.types.MalSpliceUnquote;
import com.example.mal.types.MalString;
import com.example.mal.types.MalSymbol;
import com.example.mal.types.MalType;
import com.example.mal.types.MalUnquote;
import com.example.mal.types.MalVector;
import com.example.mal.types.TypeReader;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.collection.List;
import io.vavr.control.Option;

public class reader {

    private static final List<Tuple2<Function<Reader, Boolean>, TypeReader>> READERS =
            List.of(Tuple.of(MalVector::matches,
                             MalVector::read),
                    Tuple.of(MalList::matches,
                             MalList::read),
                    Tuple.of(MalMap::matches,
                             MalMap::read),
                    Tuple.of(MalString::matches,
                             MalString::read),
                    // Reader macros
                    Tuple.of(MalQuote::matches,
                             MalQuote::read),
                    Tuple.of(MalUnquote::matches,
                             MalUnquote::read),
                    Tuple.of(MalQuasiQuote::matches,
                             MalQuasiQuote::read),
                    Tuple.of(MalSpliceUnquote::matches,
                             MalSpliceUnquote::read),
                    Tuple.of(MalDeref::matches,
                             MalDeref::read),
                    Tuple.of(MalMetadata::matches,
                             MalMetadata::read),
                    // Special forms
                    Tuple.of(MalLetStar::matches,
                             MalLetStar::read),
                    Tuple.of(MalDefBang::matches,
                             MalDefBang::read),
                    // Atoms
                    Tuple.of(MalNil::matches,
                             MalNil::read),
                    Tuple.of(MalBoolean::matches,
                             MalBoolean::read),
                    Tuple.of(MalInteger::matches,
                             MalInteger::read),
                    Tuple.of(MalKeyword::matches,
                             MalKeyword::read),
                    Tuple.of(MalSymbol::matches,
                             MalSymbol::read));

    public static MalType read_str(final String input) {
        final Reader r = Reader.of(input);
        return read_form(r)._2();
    }

    private static Tuple2<Reader, MalType> read_form(final Reader r) {
        final Option<String> t = r.peek();

        if (t.isEmpty()) {
            return Tuple.of(r,
                            Singletons.CONTINUE);
        }

        return READERS.find(rt -> rt._1.apply(r))
                      .map(Tuple2::_2)
                      .map(impl -> impl.apply(r,
                                              reader::read_form))
                      .getOrElse(Tuple.of(r,
                                          MalError.of(String.format("No suitable reader: '%s'!",
                                                                    r.peek()
                                                                     .getOrElse("<empty>")))));

    }
}
