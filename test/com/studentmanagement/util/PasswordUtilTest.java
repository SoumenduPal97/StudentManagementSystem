
package com.studentmanagement.util;

import org.junit.jupiter.api.Test;

import java.security.GeneralSecurityException;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {

    private static final int TEST_ITERATIONS = 10_000;

    @Test
    void shouldGenerateUniqueSalt() {

        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        assertNotNull(salt1);
        assertEquals(32, salt1.length());
        assertNotEquals(salt1, salt2);
    }

    @Test
    void shouldVerifyCorrectPassword()
            throws GeneralSecurityException {

        char[] password = "TestPassword123!".toCharArray();
        String salt = PasswordUtil.generateSalt();

        try {

            String hash = PasswordUtil.hashPassword(
                    password, salt, TEST_ITERATIONS
            );

            assertTrue(
                    PasswordUtil.verifyPassword(
                            password, salt, hash, TEST_ITERATIONS
                    )
            );

        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Test
    void shouldRejectIncorrectPassword()
            throws GeneralSecurityException {

        char[] correctPassword = "CorrectPassword123!".toCharArray();
        char[] wrongPassword = "WrongPassword123!".toCharArray();

        String salt = PasswordUtil.generateSalt();

        try {

            String hash = PasswordUtil.hashPassword(
                    correctPassword, salt, TEST_ITERATIONS
            );

            assertFalse(
                    PasswordUtil.verifyPassword(
                            wrongPassword, salt, hash, TEST_ITERATIONS
                    )
            );

        } finally {
            Arrays.fill(correctPassword, '\0');
            Arrays.fill(wrongPassword, '\0');
        }
    }

    @Test
    void shouldGenerateConsistentHash()
            throws GeneralSecurityException {

        char[] password = "TestPassword123!".toCharArray();
        String salt = PasswordUtil.generateSalt();

        try {

            String hash1 = PasswordUtil.hashPassword(
                    password, salt, TEST_ITERATIONS
            );

            String hash2 = PasswordUtil.hashPassword(
                    password, salt, TEST_ITERATIONS
            );

            assertEquals(hash1, hash2);

        } finally {
            Arrays.fill(password, '\0');
        }
    }

    @Test
    void shouldGenerateDifferentHashesForDifferentSalts()
            throws GeneralSecurityException {

        char[] password = "TestPassword123!".toCharArray();

        String salt1 = PasswordUtil.generateSalt();
        String salt2 = PasswordUtil.generateSalt();

        try {

            String hash1 = PasswordUtil.hashPassword(
                    password, salt1, TEST_ITERATIONS
            );

            String hash2 = PasswordUtil.hashPassword(
                    password, salt2, TEST_ITERATIONS
            );

            assertNotEquals(hash1, hash2);

        } finally {
            Arrays.fill(password, '\0');
        }
    }
}

