package com.example.mal;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.base.CharMatcher;
import com.google.common.base.Strings;

import org.immutables.value.Value;
import org.immutables.vavr.encodings.VavrEncodingEnabled;

import io.vavr.collection.List;
import io.vavr.control.Option;

@Value.Immutable
@VavrEncodingEnabled
public abstract class Reader {
    private static final Pattern PATTERN =
            Pattern.compile("[\\s ,]*(~@|[\\[\\]{}()'`~@]|\"(?:[\\\\].|[^\\\\\"])*\"?|;.*|[^\\s \\[\\]{}()'\"`~@,;]*)");

    public abstract List<String> tokens();

    @Value.Derived
    public Option<String> peek() {
        return tokens().headOption();
    }

    public Reader next() {
        return ImmutableReader.copyOf(this)
                              .withTokens(tokens().popOption()
                                                  .getOrElse(List.empty()));
    }

    public static Reader of(final String input) {
        final Matcher m = PATTERN.matcher(input);

        final ImmutableReader.Builder builder = ImmutableReader.builder();

        while (m.find()) {
            final String token = CharMatcher.whitespace()
                                            .trimFrom(m.group(1));
            if (!Strings.isNullOrEmpty(token) && !token.equalsIgnoreCase(",") && !token.startsWith(";")) {
                // System.out.println(String.format("Adding '%s'",
                // token));
                builder.addTokens(token);
            }
        }

        return builder.build();
    }
}
