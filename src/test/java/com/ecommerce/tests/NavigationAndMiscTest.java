package com.ecommerce.tests;

import com.ecommerce.pages.CartPage;
import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.TestCasesPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Navigation and UI Features Test Suite covering Official Automation Exercise Scenarios:
 * - Test Case 7: Verify Test Cases Page
 * - Test Case 10: Verify Subscription in home page
 * - Test Case 11: Verify Subscription in Cart page
 * - Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality
 * - Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality
 */
@Epic("Automation Exercise Platform")
@Feature("Navigation & Interactive UI Components")
public class NavigationAndMiscTest extends BaseTest {

    private final String subscriptionEmail = com.ecommerce.utils.TestDataFactory.generateSubscriptionEmail();

    @Test(description = "Test Case 7: Verify Test Cases Page", priority = 1)
    @Story("Test Cases Catalog Verification")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Verify home page\n3. Click on 'Test Cases' button\n4. Verify user is navigated to test cases page successfully")
    public void testCase07_VerifyTestCasesPage() {
        LOGGER.info("Starting Test Case 7: Verify Test Cases Page");

        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        TestCasesPage testCasesPage = homePage.clickTestCases();
        Assert.assertTrue(testCasesPage.isTestCasesPageVisible(), "Test Cases page heading should be visible");

        int count = testCasesPage.getTestCasesCount();
        LOGGER.info("Total test cases listed on page: {}", count);
        Assert.assertTrue(count >= 20, "Page should list at least 20 official test cases");

        LOGGER.info("Test Case 7 completed successfully!");
    }

    @Test(description = "Test Case 10: Verify Subscription in home page", priority = 2)
    @Story("Newsletter Subscription - Home Page")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Scroll down to footer\n3. Verify text 'SUBSCRIPTION'\n4. Enter email address and click arrow button\n5. Verify success message 'You have been successfully subscribed!'")
    public void testCase10_VerifySubscriptionInHomePage() {
        LOGGER.info("Starting Test Case 10: Verify Subscription in home page");

        HomePage homePage = new HomePage();
        homePage.scrollToFooter();
        Assert.assertTrue(homePage.isSubscriptionVisible(), "'SUBSCRIPTION' heading should be visible in footer");

        homePage.subscribe(subscriptionEmail);
        Assert.assertTrue(homePage.isSubscriptionSuccessVisible(), "Subscription success message should be visible");

        LOGGER.info("Test Case 10 completed successfully!");
    }

    @Test(description = "Test Case 11: Verify Subscription in Cart page", priority = 3)
    @Story("Newsletter Subscription - Cart Page")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Cart' button\n3. Scroll down to footer\n4. Verify text 'SUBSCRIPTION'\n5. Enter email address and click arrow button\n6. Verify success message 'You have been successfully subscribed!'")
    public void testCase11_VerifySubscriptionInCartPage() {
        LOGGER.info("Starting Test Case 11: Verify Subscription in Cart page");

        HomePage homePage = new HomePage();
        CartPage cartPage = homePage.clickCart();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");

        cartPage.subscribe(subscriptionEmail);
        Assert.assertTrue(cartPage.isSubscriptionSuccessVisible(), "Cart subscription success message should be visible");

        LOGGER.info("Test Case 11 completed successfully!");
    }

    @Test(description = "Test Case 25: Verify Scroll Up using 'Arrow' button and Scroll Down functionality", priority = 4)
    @Story("Scroll Up via Arrow Button")
    @Severity(SeverityLevel.MINOR)
    @Description("1. Navigate to url\n2. Scroll down to footer\n3. Verify 'SUBSCRIPTION' is visible\n4. Click on arrow at bottom right\n5. Verify page is scrolled up and header text is visible")
    public void testCase25_VerifyScrollUpUsingArrowButton() {
        LOGGER.info("Starting Test Case 25: Verify Scroll Up using Arrow button");

        HomePage homePage = new HomePage();
        homePage.scrollToFooter();
        Assert.assertTrue(homePage.isSubscriptionVisible(), "'SUBSCRIPTION' should be visible at bottom");

        homePage.clickScrollUpArrow();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        Assert.assertTrue(homePage.isTopTextVisible(), "Page top text should be visible after clicking scroll-up arrow");
        LOGGER.info("Test Case 25 completed successfully!");
    }

    @Test(description = "Test Case 26: Verify Scroll Up without 'Arrow' button and Scroll Down functionality", priority = 5)
    @Story("Scroll Up without Arrow Button")
    @Severity(SeverityLevel.MINOR)
    @Description("1. Navigate to url\n2. Scroll down to footer\n3. Verify 'SUBSCRIPTION' is visible\n4. Scroll up to top\n5. Verify page is scrolled up and header text is visible")
    public void testCase26_VerifyScrollUpWithoutArrowButton() {
        LOGGER.info("Starting Test Case 26: Verify Scroll Up without Arrow button");

        HomePage homePage = new HomePage();
        homePage.scrollToFooter();
        Assert.assertTrue(homePage.isSubscriptionVisible(), "'SUBSCRIPTION' should be visible at bottom");

        homePage.scrollUpToTop();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }

        Assert.assertTrue(homePage.isTopTextVisible(), "Page top text should be visible after scrolling to top");
        LOGGER.info("Test Case 26 completed successfully!");
    }
}
