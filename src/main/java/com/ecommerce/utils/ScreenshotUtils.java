package com.ecommerce.utils;

import com.ecommerce.drivers.DriverManager;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;

import java.util.Objects;

/**
 * Captures screenshots for test reports and visual assertion debugging.
 */
public final class ScreenshotUtils {

    private ScreenshotUtils() {}

    /**
     * Captures screenshot as byte array suitable for Allure report attachments.
     */
    public static byte[] getScreenshotBytes() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BYTES);
        }
        return new byte[0];
    }

    /**
     * Captures screenshot as Base64 encoded string.
     */
    public static String getBase64Image() {
        if (Objects.nonNull(DriverManager.getDriver())) {
            return ((TakesScreenshot) DriverManager.getDriver()).getScreenshotAs(OutputType.BASE64);
        }
        return "";
    }
}
