package com.ram.linkly.util;

public final class Base62Encoder {
    private static final String ALPHABET =
            "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static final int BASE = ALPHABET.length();

    private Base62Encoder(){

    }

    public static String encode(long value) {
        if (value < 0){
            throw new IllegalArgumentException("value must be non-negative: " + value);
        }
        if (value == 0){
            return String.valueOf(ALPHABET.charAt(0));
        }
        StringBuilder builder = new StringBuilder();
        long remainning = value;
        while (remainning > 0){
            builder.append(ALPHABET.charAt((int) (remainning % BASE)));
            remainning /= BASE;
        }
        return builder.reverse().toString();
    }

    public static long decode(String encoded){
        if (encoded == null || encoded.isEmpty()) {
            throw new IllegalArgumentException("encoded must not be empty");
        }

        long result = 0;
        for (char c : encoded.toCharArray()) {
            int digit = ALPHABET.indexOf(c);
            if (digit < 0) {
                throw new IllegalArgumentException("invalid character: " + c);
            }
            result = Math.addExact(Math.multiplyExact(result, BASE), digit);
        }
        return result;
    }
}
