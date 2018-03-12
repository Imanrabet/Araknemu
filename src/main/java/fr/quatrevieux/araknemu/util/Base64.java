package fr.quatrevieux.araknemu.util;

import java.security.InvalidParameterException;

/**
 * Utility class for Dofus Pseudo base 64
 */
final public class Base64 {
    final static private char[] CHARSET = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's',
        't', 'u', 'v', 'w', 'x', 'y', 'z', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U',
        'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '_'};

    /**
     * Get int value of base64 char
     * @param c Char to convert
     */
    static public int ord(char c) {
        if (c >= 'a' && c <= 'z') {
            return c - 'a';
        }

        if (c >= 'A' && c <= 'Z') {
            return c - 'A' + 26;
        }

        if (c >= '0' && c <= '9') {
            return c - '0' + 52;
        }

        switch (c) {
            case '-':
                return 62;
            case '_':
                return 63;
            default:
                throw new InvalidParameterException("Invalid char value");
        }
    }

    /**
     * Get the base 64 character for the value
     */
    static public char chr(int value) {
        return CHARSET[value];
    }

    /**
     * Encode an int value to pseudo base 64
     *
     * @param value Value to encode
     * @param length The expected result length
     *
     * @return The encoded value
     */
    static public String encode(int value, int length) {
        char[] encoded = new char[length];

        for (int i = length - 1; i >= 0; --i) {
            encoded[i] = CHARSET[value & 63];
            value >>= 6;
        }

        return new String(encoded);
    }
}
