package com.ecommerce.drivers;

import org.openqa.selenium.WebDriver;

import java.util.Objects;

/**
 * Thread-safe WebDriver management using ThreadLocal to isolate instances per thread.
 */
public final class DriverManager {

    private DriverManager() {}

    private static final ThreadLocal<WebDriver> DRIVER = new ThreadLocal<>();

    public static WebDriver getDriver() {
        return DRIVER.get();
    }

    public static void setDriver(WebDriver driverRef) {
        if (Objects.nonNull(driverRef)) {
            DRIVER.set(driverRef);
        }
    }

    public static void unload() {
        DRIVER.remove();
    }
}
