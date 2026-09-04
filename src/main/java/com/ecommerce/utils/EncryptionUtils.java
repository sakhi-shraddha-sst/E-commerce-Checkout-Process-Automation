package com.ecommerce.utils;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * Enterprise security utility for encrypting and decrypting sensitive credentials
 * (passwords, access keys, tokens) using AES-256 GCM authenticated encryption.
 *
 * Standalone Java SE utility: can be executed directly from IDE or terminal.
 * Prevents plain-text password exposure in git repositories and test files.
 */
public final class EncryptionUtils {

    private static final Logger LOGGER = Logger.getLogger(EncryptionUtils.class.getName());
    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;

    // Master key dynamically resolved from Environment variable or fallback default
    // salt
    private static final String DEFAULT_SALT = "Automation_Enterprise_Secret_Key_2026";
    private static final String MASTER_KEY = resolveMasterKey();

    private EncryptionUtils() {
    }

    private static String resolveMasterKey() {
        String envKey = System.getenv("APP_MASTER_KEY");
        if (envKey != null && !envKey.trim().isEmpty()) {
            return envKey.trim();
        }
        String sysKey = System.getProperty("app.master.key");
        if (sysKey != null && !sysKey.trim().isEmpty()) {
            return sysKey.trim();
        }
        return DEFAULT_SALT;
    }

    private static SecretKey deriveKey(String secret) {
        try {
            MessageDigest sha = MessageDigest.getInstance("SHA-256");
            byte[] keyBytes = sha.digest(secret.getBytes(StandardCharsets.UTF_8));
            return new SecretKeySpec(keyBytes, "AES");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm unavailable for key derivation", e);
        }
    }

    /**
     * Encrypts plain text string using AES-256 GCM and returns ENC(...) formatted
     * token.
     */
    public static String encrypt(String plainText) {
        if (plainText == null || plainText.isEmpty()) {
            return plainText;
        }
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.ENCRYPT_MODE, deriveKey(MASTER_KEY), spec);

            byte[] cipherText = cipher.doFinal(plainText.getBytes(StandardCharsets.UTF_8));

            byte[] combined = new byte[iv.length + cipherText.length];
            System.arraycopy(iv, 0, combined, 0, iv.length);
            System.arraycopy(cipherText, 0, combined, iv.length, cipherText.length);

            return "ENC(" + Base64.getEncoder().encodeToString(combined) + ")";
        } catch (Exception e) {
            LOGGER.severe("Encryption failed for text: " + e.getMessage());
            throw new RuntimeException("Encryption failure", e);
        }
    }

    /**
     * Decrypts an encrypted token. Handles ENC(...) format or raw Base64 payload.
     * If the text is not encrypted, returns it unchanged.
     */
    public static String decrypt(String cipherToken) {
        if (cipherToken == null || cipherToken.isEmpty()) {
            return cipherToken;
        }

        String rawToken = cipherToken.trim();
        if (rawToken.startsWith("ENC(") && rawToken.endsWith(")")) {
            rawToken = rawToken.substring(4, rawToken.length() - 1).trim();
        } else {
            // Not in ENC(...) format, return as-is
            return cipherToken;
        }

        try {
            byte[] decoded = Base64.getDecoder().decode(rawToken);
            if (decoded.length <= GCM_IV_LENGTH) {
                throw new IllegalArgumentException("Invalid encrypted payload length");
            }

            byte[] iv = new byte[GCM_IV_LENGTH];
            System.arraycopy(decoded, 0, iv, 0, GCM_IV_LENGTH);

            int cipherLength = decoded.length - GCM_IV_LENGTH;
            byte[] cipherText = new byte[cipherLength];
            System.arraycopy(decoded, GCM_IV_LENGTH, cipherText, 0, cipherLength);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            GCMParameterSpec spec = new GCMParameterSpec(GCM_TAG_LENGTH, iv);
            cipher.init(Cipher.DECRYPT_MODE, deriveKey(MASTER_KEY), spec);

            byte[] plainTextBytes = cipher.doFinal(cipherText);
            return new String(plainTextBytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            LOGGER.severe("Failed to decrypt token: " + e.getMessage());
            throw new RuntimeException("Decryption failed for token: " + cipherToken, e);
        }
    }

    /**
     * Simple CLI helper to generate an encrypted token for passwords or keys.
     */
    public static void main(String[] args) {
        String input = (args.length > 0) ? args[0] : "oauth-shraddha.st.web-60361";
        String encrypted = encrypt(input);
        System.out.println("Original  : " + input);
        System.out.println("Encrypted : " + encrypted);
        System.out.println("Decrypted : " + decrypt(encrypted));
    }
}
