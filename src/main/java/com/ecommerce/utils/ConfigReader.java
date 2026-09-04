package com.ecommerce.utils;

import com.ecommerce.constants.FrameworkConstants;
import com.ecommerce.enums.ConfigProperties;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;

/**
 * Utility to load and retrieve key-value pairs from config.properties into memory.
 */
public final class ConfigReader {

    private ConfigReader() {}

    private static final Properties PROPERTIES = new Properties();
    private static final Map<String, String> CONFIG_MAP = new HashMap<>();

    static {
        try (FileInputStream fileInputStream = new FileInputStream(FrameworkConstants.getConfigFilePath())) {
            PROPERTIES.load(fileInputStream);
            for (Map.Entry<Object, Object> entry : PROPERTIES.entrySet()) {
                CONFIG_MAP.put(String.valueOf(entry.getKey()).trim().toLowerCase(), String.valueOf(entry.getValue()).trim());
            }
        } catch (FileNotFoundException e) {
            throw new RuntimeException("Property file not found at: " + FrameworkConstants.getConfigFilePath(), e);
        } catch (IOException e) {
            throw new RuntimeException("Failed to read property file at: " + FrameworkConstants.getConfigFilePath(), e);
        }
    }

    /**
     * Retrieves value using ConfigProperties enum key.
     */
    public static String get(ConfigProperties key) {
        if (Objects.isNull(key)) {
            throw new IllegalArgumentException("Property name cannot be null");
        }
        return get(key.name().toLowerCase());
    }

    /**
     * Retrieves value using raw string key.
     */
    public static String get(String key) {
        if (Objects.isNull(key) || key.trim().isEmpty()) {
            throw new IllegalArgumentException("Property key cannot be null or empty");
        }

        String trimmedKey = key.trim();

        // 1. System property override takes highest priority (e.g. -Drunmode=saucelabs)
        String systemProp = System.getProperty(trimmedKey.toLowerCase());
        if (Objects.isNull(systemProp)) {
            systemProp = System.getProperty(trimmedKey);
        }
        if (Objects.nonNull(systemProp) && !systemProp.trim().isEmpty()) {
            return resolveValue(systemProp.trim());
        }

        // 2. System Environment Variable (e.g. BROWSERSTACK_ACCESSKEY, SAUCELABS_ACCESSKEY)
        String envVar = System.getenv(trimmedKey.toUpperCase());
        if (Objects.isNull(envVar)) {
            envVar = System.getenv(trimmedKey);
        }
        if (Objects.nonNull(envVar) && !envVar.trim().isEmpty()) {
            return resolveValue(envVar.trim());
        }

        // 3. Fall back to config.properties
        String value = CONFIG_MAP.get(trimmedKey.toLowerCase());
        if (Objects.isNull(value)) {
            throw new RuntimeException("Property name '" + key + "' was not found in config.properties, Environment variables, or System properties");
        }
        return resolveValue(value);
    }

    /**
     * Resolves value: if encrypted with ENC(...), decrypts it via EncryptionUtils;
     * otherwise returns value unchanged.
     */
    private static String resolveValue(String raw) {
        if (raw != null && raw.startsWith("ENC(") && raw.endsWith(")")) {
            return EncryptionUtils.decrypt(raw);
        }
        return raw;
    }
}
