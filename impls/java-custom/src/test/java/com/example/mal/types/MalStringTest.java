
package com.example.mal.types;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import io.vavr.control.Option;

public class MalStringTest {

    @Test
    void unescapeTest() {
        assertEquals(Option.of("foo"),
                     MalString.unescape(Option.of("\"foo\"")));
        assertEquals(Option.of(" a\\b "),
                     MalString.unescape(Option.of("\" a\\\\b \"")));
        assertEquals(Option.of(" a\nb "),
                     MalString.unescape(Option.of("\" a\\nb \"")));
    }

    @Test
    void escapeTest() {
        assertEquals("\"hello\"",
                     MalString.escape("hello"));
        assertEquals("\"hel\\nlo\"",
                     MalString.escape("hel\nlo"));
    }

    private static String roundTrip(final String input) {
        return MalString.unescape(Option.some(MalString.escape(input)))
                        .get();
    }

    @Test
    void roundTripTest() {
        assertEquals("hello",
                     roundTrip(roundTrip("hello")));
        assertEquals("h\"el\\lo\n",
                     roundTrip(roundTrip("h\"el\\lo\n")));

    }
}
