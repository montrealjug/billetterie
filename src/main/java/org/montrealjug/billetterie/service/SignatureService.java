// SPDX-License-Identifier: Apache-2.0
package org.montrealjug.billetterie.service;

import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.Signature;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import org.montrealjug.billetterie.config.BilletterieProperties;
import org.springframework.stereotype.Service;

/**
 * Service for signing strings using RSA private key.
 */
@Service
public class SignatureService {

    private final BilletterieProperties properties;
    private PrivateKey privateKey;

    public SignatureService(BilletterieProperties properties) {
        this.properties = properties;
    }

    /**
     * Signs the given string using the RSA private key.
     *
     * @param content the string to sign
     * @return the Base64-encoded signature of the string
     * @throws Exception if there's an error during the signing process
     */
    public String sign(String content) throws Exception {
        if (privateKey == null) {
            initializePrivateKey();
        }

        Signature signature = Signature.getInstance("SHA256withRSA");
        signature.initSign(privateKey);
        signature.update(content.getBytes(StandardCharsets.UTF_8));
        byte[] signedBytes = signature.sign();

        return Base64.getEncoder().encodeToString(signedBytes);
    }

    /**
     * Signs the given string using the RSA private key, then trims the result to 64 characters
     * and removes non-alphanumeric characters.
     *
     * @param content the string to sign
     * @return the trimmed and cleaned signature
     * @throws Exception if there's an error during the signing process
     */
    public String signAndTrim(String content) throws Exception {
        String signature = sign(content);

        // Trim to 64 characters if longer
        if (signature.length() > 64) {
            signature = signature.substring(0, 64);
        }

        // Remove non-alphanumeric characters
        return signature.replaceAll("[^a-zA-Z0-9]", "");
    }

    private void initializePrivateKey() throws Exception {
        String rsaKeyPem = properties.rsaKey();

        // Remove PEM headers and newlines
        String privateKeyContent = rsaKeyPem
            .replace("-----BEGIN PRIVATE KEY-----", "")
            .replace("-----END PRIVATE KEY-----", "")
            .replaceAll("\\s", "");

        // Decode the Base64 encoded key
        byte[] privateKeyBytes = Base64.getDecoder().decode(privateKeyContent);

        // Create the private key
        KeyFactory keyFactory = KeyFactory.getInstance("RSA");
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(privateKeyBytes);
        privateKey = keyFactory.generatePrivate(keySpec);
    }
}
