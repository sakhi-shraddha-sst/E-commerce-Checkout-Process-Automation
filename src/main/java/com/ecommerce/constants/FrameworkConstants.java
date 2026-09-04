package com.ecommerce.constants;

import java.io.File;

/**
 * Global framework constants holding static paths, default timeouts, and sheet names.
 */
public final class FrameworkConstants {

    private FrameworkConstants() {}

    private static final String USER_DIR = System.getProperty("user.dir");
    private static final String RESOURCES_PATH = USER_DIR + File.separator + "src" + File.separator + "test" + File.separator + "resources" + File.separator;
    private static final String CONFIG_FILE_PATH = RESOURCES_PATH + "config" + File.separator + "config.properties";
    private static final String TEST_DATA_EXCEL_PATH = RESOURCES_PATH + "testdata" + File.separator + "testdata.xlsx";
    private static final String ALLURE_RESULTS_PATH = USER_DIR + File.separator + "target" + File.separator + "allure-results";

    private static final int EXPLICIT_WAIT = 20;
    private static final int SHORT_WAIT = 5;
    private static final int LONG_WAIT = 35;

    private static final String CHECKOUT_DATA_SHEET = "CheckoutData";
    private static final String USER_DATA_SHEET = "UserData";

    public static String getConfigFilePath() {
        return CONFIG_FILE_PATH;
    }

    public static String getTestDataExcelPath() {
        return TEST_DATA_EXCEL_PATH;
    }

    public static String getAllureResultsPath() {
        return ALLURE_RESULTS_PATH;
    }

    public static int getExplicitWait() {
        return EXPLICIT_WAIT;
    }

    public static int getShortWait() {
        return SHORT_WAIT;
    }

    public static int getLongWait() {
        return LONG_WAIT;
    }

    public static String getCheckoutDataSheet() {
        return CHECKOUT_DATA_SHEET;
    }

    public static String getUserDataSheet() {
        return USER_DATA_SHEET;
    }
}
