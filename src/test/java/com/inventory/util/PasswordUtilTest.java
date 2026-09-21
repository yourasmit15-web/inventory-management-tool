package com.inventory.util;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PasswordUtilTest {
    @Test
    void hashAndVerifyRoundTripWorks() {
        String password = "test-password-123";
        String hash = PasswordUtil.hash(password);

        assertNotNull(hash);
        assertNotEquals(password, hash);
        assertTrue(PasswordUtil.verify(password, hash));
        assertFalse(PasswordUtil.verify("wrong-password", hash));
    }
}
