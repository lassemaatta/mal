package com.example.mal.types.atoms;

import java.util.function.Function;

import com.example.mal.Reader;
import com.example.mal.types.MalError;
import com.example.mal.types.MalType;
import com.google.common.collect.Lists;

import org.immutables.value.Value;
import org.immutables.value.Value.Lazy;

import io.vavr.Tuple;
import io.vavr.Tuple2;
import io.vavr.control.Option;

@Value.Immutable
public abstract class MalString implements MalType {

    private static final Character START_TOKEN = '"';
    private static final Character END_TOKEN = '"';

    public abstract String content();

    @Override
    @Lazy
    public String pr() {
        return escape(content());
    }

    public static boolean matches(final Reader r) {
        return r.peek()
                .map(token -> START_TOKEN.equals(token.charAt(0)))
                .getOrElse(false);
    }

    private static MalString of(final String token) {
        return ImmutableMalString.builder()
                                 .content(token)
                                 .build();
    }

    public static String escape(final String content) {
        final StringBuilder sb = new StringBuilder(content.length());

        sb.append("\"");
        for (final Character ch : Lists.charactersOf(content)) {
            if (ch == '\"') {
                sb.append("\\\"");
            } else if (ch == '\n') {
                sb.append("\\n");
            } else if (ch == '\\') {
                sb.append("\\\\");
            } else {
                sb.append(ch);
            }
        }
        sb.append("\"");
        return sb.toString();
    }

    private static Option<String> unescape_str(final String token) {
        final StringBuilder sb = new StringBuilder(token.length());
        boolean escaping = false;
        for (final Character ch : Lists.charactersOf(token)) {
            if (ch == '\\' && !escaping) {
                escaping = true;
            } else if (escaping) {
                if (ch == '"') {
                    sb.append('"');
                    escaping = false;
                } else if (ch == 'n') {
                    sb.append('\n');
                    escaping = false;
                } else if (ch == '\\') {
                    sb.append('\\');
                    escaping = false;
                } else {
                    return Option.none();
                }
            } else {
                sb.append(ch);
            }
        }
        if (escaping) {
            return Option.none();
        }
        return Option.of(sb.toString());
    }

    public static Option<String> unescape(final Option<String> input) {
        return input.filter(token -> token.length() >= 2)
                    .filter(token -> END_TOKEN.equals(token.charAt(token.length() - 1)))
                    .map(token -> token.substring(1,
                                                  token.length() - 1))
                    .flatMap(MalString::unescape_str);
    }

    public static Tuple2<Reader, MalType> read(final Reader r,
                                               final Function<Reader, Tuple2<Reader, MalType>> formReader) {
        return r.peek()
                .transform(MalString::unescape)
                .map(token -> Tuple.of(r.next(),
                                       (MalType) of(token)))
                .getOrElse(Tuple.of(r,
                                    MalError.of("EOF while reading string")));
    }
}
