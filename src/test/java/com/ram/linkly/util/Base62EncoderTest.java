package com.ram.linkly.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class Base62EncoderTest {

    @Test
    void encodesZeroAsFirstSymbol() {
        assertEquals("0", Base62Encoder.encode(0));
    }

    @Test
    void encodesBoundaryValues() {
        assertEquals("9", Base62Encoder.encode(9));
        assertEquals("A", Base62Encoder.encode(10));
        assertEquals("z", Base62Encoder.encode(61));
        assertEquals("10", Base62Encoder.encode(62));
    }

    @Test
    void decodesKnownValues() {
        assertEquals(0, Base62Encoder.decode("0"));
        assertEquals(61, Base62Encoder.decode("z"));
        assertEquals(62, Base62Encoder.decode("10"));
    }

    @Test
    void roundTripKeepsValue() {
        long[] values = {0, 1, 61, 62, 1_000, 123_456_789L, Long.MAX_VALUE};
        for (long value : values) {
            assertEquals(value, Base62Encoder.decode(Base62Encoder.encode(value)));
        }
    }

    @Test
    void rejectsNegativeValues() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.encode(-1));
    }

    @Test
    void rejectsInvalidCharacters() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode("ab-c"));
    }

    @Test
    void rejectsEmptyInput() {
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode(""));
        assertThrows(IllegalArgumentException.class, () -> Base62Encoder.decode(null));
    }

    @Test
    void detectsOverflow() {
        assertThrows(ArithmeticException.class, () -> Base62Encoder.decode("zzzzzzzzzzzzzzzz"));
    }
}