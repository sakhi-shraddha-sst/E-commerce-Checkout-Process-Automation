package com.ecommerce.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

/**
 * Utility for parsing and deserializing JSON scenario test data into strongly-typed POJO models.
 */
public final class JsonUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(JsonUtils.class);
    private static final ObjectMapper MAPPER = new ObjectMapper();

    private JsonUtils() {}

    /**
     * Deserializes JSON file into the specified Java class.
     * Searches both absolute file paths and relative resource paths.
     */
    public static <T> T deserialize(String jsonFilePath, Class<T> targetClass) {
        try {
            File file = new File(jsonFilePath);
            if (file.exists()) {
                try (InputStream is = new FileInputStream(file)) {
                    return MAPPER.readValue(is, targetClass);
                }
            }

            // Fallback to ClassLoader resource
            ClassLoader cl = Thread.currentThread().getContextClassLoader();
            try (InputStream is = cl.getResourceAsStream(jsonFilePath)) {
                if (is != null) {
                    return MAPPER.readValue(is, targetClass);
                }
            }

            throw new RuntimeException("JSON file not found at path or classpath: " + jsonFilePath);
        } catch (Exception e) {
            LOGGER.error("Failed to deserialize JSON file [{}] to class [{}]: {}", jsonFilePath, targetClass.getSimpleName(), e.getMessage());
            throw new RuntimeException("JSON Deserialization error for: " + jsonFilePath, e);
        }
    }
}
