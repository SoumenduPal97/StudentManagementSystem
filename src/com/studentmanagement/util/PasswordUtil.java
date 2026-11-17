
package com.studentmanagement.util;

import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

public final class PasswordUtil {

    private static final int ITERATIONS = 600000;
    private static final int KEY_LENGTH = 256;
    private static final int SALT_LENGTH = 16;

    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() {
        
    }

    public static String generateSalt() {

        byte[] salt = new byte[SALT_LENGTH];

        RANDOM.nextBytes(salt);

        return toHex(salt);
    }

    public static String hashPassword(
            char[] password,
            String saltHex,
            int iterations
    ) throws GeneralSecurityException {

        if (password == null || iterations <= 0) {
            throw new IllegalArgumentException(
                    "Invalid password or iteration count."
            );
        }

        byte[] salt = fromHex(saltHex);

        PBEKeySpec spec = new PBEKeySpec(
                password,
                salt,
                iterations,
                KEY_LENGTH
        );

        try {

            SecretKeyFactory factory =
                    SecretKeyFactory.getInstance(
                            "PBKDF2WithHmacSHA256"
                    );

            byte[] hash = factory
                    .generateSecret(spec)
                    .getEncoded();

            return toHex(hash);

        } finally {

            spec.clearPassword();
            Arrays.fill(salt, (byte) 0);
        }
    }

    public static boolean verifyPassword(
            char[] password,
            String saltHex,
            String storedHash,
            int iterations
    ) throws GeneralSecurityException {

        String calculatedHash = hashPassword(
                password,
                saltHex,
                iterations
        );

        byte[] calculatedBytes = fromHex(calculatedHash);
        byte[] storedBytes = fromHex(storedHash);

        try {

            return MessageDigest.isEqual(
                    calculatedBytes,
                    storedBytes
            );

        } finally {

            Arrays.fill(calculatedBytes, (byte) 0);
            Arrays.fill(storedBytes, (byte) 0);
        }
    }

    private static String toHex(byte[] bytes) {

        StringBuilder result = new StringBuilder();

        for (byte b : bytes) {

            result.append(
                    String.format("%02x", b & 0xff)
            );
        }

        return result.toString();
    }

    private static byte[] fromHex(String hex) {

        if (hex == null || hex.length() % 2 != 0
                || !hex.matches("[0-9a-fA-F]+")) {

            throw new IllegalArgumentException(
                    "Invalid hexadecimal value."
            );
        }

        byte[] result = new byte[hex.length() / 2];

        for (int i = 0; i < result.length; i++) {

            int index = i * 2;

            result[i] = (byte) Integer.parseInt(
                    hex.substring(index, index + 2),
                    16
            );
        }

        return result;
    }

    public static int getIterations() {
        return ITERATIONS;
    }
}
