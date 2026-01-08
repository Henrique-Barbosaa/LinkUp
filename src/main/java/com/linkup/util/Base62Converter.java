package com.linkup.util;

import org.springframework.stereotype.Component;

@Component
public class Base62Converter {

    private static final String CHARACTERS = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int BASE = CHARACTERS.length();

    public String encode(long input) {
        if (input == 0) {
            return String.valueOf(CHARACTERS.charAt(0));
        }

        StringBuilder sb = new StringBuilder();
        while (input > 0) {
            int remainder = (int) (input % BASE);
            sb.append(CHARACTERS.charAt(remainder));
            input /= BASE;
        }
        return sb.reverse().toString();
    }
}