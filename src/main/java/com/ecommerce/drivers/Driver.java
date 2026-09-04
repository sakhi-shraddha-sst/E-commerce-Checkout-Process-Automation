package com.ecommerce.drivers;

import com.ecommerce.enums.ConfigProperties;
import com.ecommerce.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.Objects;

/**
 * High-level driver lifecycle controller for initializing and quitting WebDriver instances.
 */
public final class Driver {

    private static final Logger LOGGER = LoggerFactory.getLogger(Driver.class);

    private Driver() {}

    /**
     * Initializes the WebDriver using default or passed browser and configuration run mode.
     */
    public static void initDriver(String browser) {
        String runMode = ConfigReader.get(ConfigProperties.RUNMODE);
        initDriver(browser, runMode);
    }

    /**
     * Initializes the WebDriver with specified browser and run mode.
     */
    public static void initDriver(String browser, String runMode) {
        if (Objects.isNull(DriverManager.getDriver())) {
            String browserToUse = (browser != null && !browser.trim().isEmpty())
                    ? browser
                    : ConfigReader.get(ConfigProperties.BROWSER);
            String modeToUse = (runMode != null && !runMode.trim().isEmpty())
                    ? runMode
                    : ConfigReader.get(ConfigProperties.RUNMODE);

            WebDriver driver = DriverFactory.createDriver(browserToUse, modeToUse);
            if (!"local".equalsIgnoreCase(modeToUse) && driver instanceof org.openqa.selenium.remote.RemoteWebDriver) {
                try {
                    ((org.openqa.selenium.remote.RemoteWebDriver) driver).setFileDetector(new org.openqa.selenium.remote.LocalFileDetector());
                } catch (Exception e) {
                    LOGGER.debug("File detector could not be set: {}", e.getMessage());
                }
            }
            DriverManager.setDriver(driver);

            DriverManager.getDriver().manage().timeouts().implicitlyWait(Duration.ofSeconds(0));
            DriverManager.getDriver().manage().timeouts().pageLoadTimeout(Duration.ofSeconds(60));

            LOGGER.info("Driver initialized successfully for Thread ID: {}", Thread.currentThread().getId());
        }
    }

    /**
     * Closes and quits the active WebDriver instance and cleans ThreadLocal.
     */
    public static void quitDriver() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            LOGGER.info("Tearing down Driver for Thread ID: {}", Thread.currentThread().getId());
            DriverManager.getDriver().quit();
            DriverManager.unload();
        }
    }
}
