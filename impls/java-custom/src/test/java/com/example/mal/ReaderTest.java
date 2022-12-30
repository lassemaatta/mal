
package com.example.mal;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

public class ReaderTest {

    @Test
    @Disabled
    void foo() {
        assertEquals(1,
                     Reader.of("   ( ) "));
    }

}
