package com.ecommerce.utils;

import com.ecommerce.constants.FrameworkConstants;
import org.testng.annotations.DataProvider;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * DataProvider supplier providing Excel data mapped dynamically for TestNG test methods.
 */
public final class DataProviderUtils {

    private DataProviderUtils() {}

    /**
     * Provides test data for Checkout tests from the Excel 'CheckoutData' sheet.
     * Filters for execute='yes' or matches test method name if specified.
     */
    @DataProvider(name = "checkoutData", parallel = false)
    public static Object[][] getCheckoutData(Method method) {
        List<Map<String, String>> fullList = ExcelUtils.getTestData(FrameworkConstants.getCheckoutDataSheet());
        List<Map<String, String>> filteredList = new ArrayList<>();

        for (Map<String, String> row : fullList) {
            String execute = row.getOrDefault("execute", "yes");
            String testName = row.getOrDefault("testname", "");

            if ("yes".equalsIgnoreCase(execute)) {
                if (testName.isEmpty() || testName.equalsIgnoreCase(method.getName())) {
                    filteredList.add(row);
                }
            }
        }

        Object[][] data = new Object[filteredList.size()][1];
        for (int i = 0; i < filteredList.size(); i++) {
            data[i][0] = filteredList.get(i);
        }

        return data;
    }

    /**
     * Provides multiple user credentials and product searches for data-driven testing.
     */
    @DataProvider(name = "multipleUsersAndProducts")
    public static Object[][] getMultipleUsersAndProducts() {
        List<Map<String, String>> fullList = ExcelUtils.getTestData(FrameworkConstants.getUserDataSheet());
        List<Map<String, String>> activeRows = new ArrayList<>();

        for (Map<String, String> row : fullList) {
            if ("yes".equalsIgnoreCase(row.getOrDefault("execute", "yes"))) {
                activeRows.add(row);
            }
        }

        Object[][] data = new Object[activeRows.size()][1];
        for (int i = 0; i < activeRows.size(); i++) {
            data[i][0] = activeRows.get(i);
        }

        return data;
    }
}
