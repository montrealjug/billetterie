// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.Signature;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.montrealjug.billetterie.config.BilletterieProperties;

class SignatureServiceTest {

    @Mock
    private BilletterieProperties properties;

    private SignatureService signatureService;

    // Test RSA key pair (only for testing)
    private static final String TEST_PRIVATE_KEY =
        """
            -----BEGIN PRIVATE KEY-----
            MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQC1Ni7AvSg7afrp
            doa4mkstme3qKfOXvIm52RoHkE+24nyipe7mjTS1ysRgEMRKXiSgW0K+mFFHiCDP
            RpIlZP2FIwFOQJeetjElW0Sk0hkvvjbene5dIXKNWCZl8z3B4kzFV0debIkoFC9t
            izYQ8BXU0836Ors0HMSHhS3XYatMZbM/dvVz8RiDc19kd5Y76/FxZB1oxy+0RpIe
            lTV6KfjHOBYFLV+1pZstdekoP45NXIeSFYManQn16TtlgiZVafS9sjK8Hd9JoXLt
            wu53W2Dc7XMXagcshU5YExmvjs2I4ZoGQi5bcq12IWKBgMOK+XUZu8HzeqGcuH9W
            8XLXzJPfAgMBAAECggEAWo5ZndLJXKuRCoNDPtRINmEiji7pvs2mq9us0NPSqiuT
            qjou4UL2cNSD/xfR5/IU7pe8in+Wup7x+nMjTJGjyZkdKWxI+LTGaYsrCgy+Lmgp
            hmU4/Yxd8Pl4suDz30SGJeYAUdT+U6uGqgD0A1HfLwyrdd1MuPWZVrn8lxIFZvbi
            cCe2AhB/aRVadpTFCZ+F6j22gRcZ7dYnS/sH8jXXuzwZ9JR820XH9p6gq7Chyuq8
            BA+r94En1M0fpKf0CC6+Bh2kByimLAMKR924ukDeU3pZF34DQaQBhgY1aexZ0cNW
            BwXOOHDNWfR3wK906pDZKj2u8mrajblWsxuEnIf4iQKBgQDW6VJMUNfRL9FAsbuh
            m4R9huGMJn1KIktJnV3zn29VGKKfzUfIZF2oBDuOkGkeurvPG0dQ35Wg0MMa9sVD
            msEz1pUOy14ihm+cDFmXaSDPGveXJkBe345+kW36ArnbXjaCFWISIN+sFHB8Ufpg
            XW7rjuBQk3HU+3mb5Mzrj3mDeQKBgQDX23OWmgOGZxhAwYWC8hN/XIvnC2kuzx56
            DLissdp5Jp005Zp0K/SSSi6taoaisoUGz+0BYv+haO17zKuI+x4EhaR8KDX3Q98B
            +H19n40+5vCRFD8+wTouqI6Ltla3bGjFStu/JVsPJg8bpoc9F0n3QK2au3yziKi+
            EiP3xJrkFwKBgBeGdvHPr7BAccGJMybfpUMwbqQu2mwxENjAFzbB7yf7iGHB0OfI
            xM/Nls9mU3t/qWtkawwZTYHLGHBtLu7Vk+yewrZZ4LYazUDhwTTn1yWUqCGtmEJK
            aojEVquVfM7co07eFFwzqQhnPGD/gE7oK2oxu7BWU5Gi7Y75Hs1yaTTRAoGBAKoP
            4mxHoPlV5fy2uupksEnKbwqzqcXQNlGwDhs48Eg8zORs9JgMPV64BThpUfOCtF2e
            mTpbdc0ELv43TPnZ5ldntyR+Ra4ukdcqoCvF9XEWX3fdvMpDUASMlemq2X0fcxfl
            F2XsF2bC9GozdB8EZyjekyfyCJgl1dQR1LaS9fk3AoGAFbqmcmxwjr+bTn6vFyci
            u3va8Ghlw2b3hxlH9ru0nH1WLsUikJBEHlax3o4vv3LJuJ87kOervkAn3kvldKjX
            KEh5GMyeudu4hV7Yv7xF91aYp7CxFmV9bmdNooIk4eCI7bx07QFJODmhZtTjyqba
            vf8G7UCtrr28OTQGVbhUdx8=
            -----END PRIVATE KEY-----\
            """;

    // Corresponding public key for verification (only for testing)
    private static final String TEST_PUBLIC_KEY =
        """
            MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAtTYuwL0oO2n66XaGuJpL\
            LZnt6inzl7yJudkaB5BPtuJ8oqXu5o00tcrEYBDESl4koFtCvphRR4ggz0aSJWT9\
            hSMBTkCXnrYxJVtEpNIZL7423p3uXSFyjVgmZfM9weJMxVdHXmyJKBQvbYs2EPAV\
            1NPN+jq7NBzEh4Ut12GrTGWzP3b1c/EYg3NfZHeWO+vxcWQdaMcvtEaSHpU1ein4\
            xzgWBS1ftaWbLXXpKD+OTVyHkhWDGp0J9ek7ZYImVWn0vbIyvB3fSaFy7cLud1tg\
            3O1zF2oHLIVOWBMZr47NiOGaBkIuW3KtdiFigYDDivl1GbvB83qhnLh/VvFy18yT\
            3wIDAQAB\
            """;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(properties.rsaKey()).thenReturn(TEST_PRIVATE_KEY);
        signatureService = new SignatureService(properties);
    }

    @Test
    void sign_shouldReturnValidSignature() throws Exception {
        // Given
        String content = "Test content to sign";

        // When
        String signature = signatureService.sign(content);

        // Then
        assertNotNull(signature);
        assertTrue(verifySignature(content, signature));
    }

    @Test
    void signAndTrim_shouldReturnTrimmedAndCleanedSignature() throws Exception {
        // Given
        String content = "Test content to sign";

        // When
        String signature = signatureService.signAndTrim(content);

        // Then
        assertNotNull(signature);
        assertTrue(signature.length() <= 64);
        assertTrue(signature.matches("[a-zA-Z0-9]+"));

        // Verify that the original signature (before trimming and cleaning) is valid
        String originalSignature = signatureService.sign(content);
        assertTrue(verifySignature(content, originalSignature));
    }

    private boolean verifySignature(String content, String signatureBase64) throws Exception {
        // Decode the Base64 encoded signature
        byte[] signatureBytes = Base64.getDecoder().decode(signatureBase64);

        // Create the public key for verification
        byte[] publicKeyBytes = Base64.getDecoder().decode(TEST_PUBLIC_KEY);

        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(publicKeyBytes);
        PublicKey publicKey = keyFactory.generatePublic(keySpec);

        // Verify the signature
        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initVerify(publicKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));

        return signature.verify(signatureBytes);
    }
}
