package com.cgovern.cgovern;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import org.junit.jupiter.api.Test;

import com.cgovern.cgovern.service.HmacSigner;

class HmacSignerTest {

    @Test
    void signaturesAreStableAndSecretSensitive() {
        String a = HmacSigner.sign("secret", "1", "{\"ok\":true}");
        String b = HmacSigner.sign("secret", "1", "{\"ok\":true}");
        String c = HmacSigner.sign("other", "1", "{\"ok\":true}");
        assertEquals(a, b);
        assertNotEquals(a, c);
        assertEquals(64, a.length());
    }
}
