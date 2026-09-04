package com.ecommerce.tests;

import com.ecommerce.pages.*;
import com.ecommerce.utils.DataProviderUtils;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Map;

/**
 * Data-Driven Checkout Testing aligned with Automation Exercise,
 * driven by Excel spreadsheet testdata.xlsx.
 */
@Epic("Automation Exercise Platform")
@Feature("Data-Driven Checkout Testing")
public class DataDrivenCheckoutTest extends BaseTest {

    @Test(dataProvider = "multipleUsersAndProducts", dataProviderClass = DataProviderUtils.class,
          description = "Execute data-driven checkout flows using multiple credentials and product searches from Excel")
    @Story("Excel Data-Driven Order Placement")
    @Severity(SeverityLevel.CRITICAL)
    @Description("Iterates through test cases supplied by Excel spreadsheet, authenticating users, searching products, adding to cart, and placing orders.")
    public void testDataDrivenCheckout(Map<String, String> testData) {
        String testCase = testData.get("testcase");
        String email = testData.get("email");
        String password = testData.get("password");
        String product = testData.get("product");
        String cardName = testData.getOrDefault("card_name", "Test User");
        String cardNumber = testData.getOrDefault("card_number", "4111111111111111");
        String cvc = testData.getOrDefault("cvc", "311");
        String expMonth = testData.getOrDefault("exp_month", "12");
        String expYear = testData.getOrDefault("exp_year", "2028");

        LOGGER.info("Executing Data-Driven Test Case: [{}] for User: [{}] and Product: [{}]", testCase, email, product);

        HomePage homePage = new HomePage();
        LoginPage loginPage = homePage.clickSignupLogin();

        // 1. Authenticate with credentials from Excel
        homePage = loginPage.login(email, password);
        Assert.assertTrue(homePage.isLoggedInAsVisible(), "User should be logged in as verified at top");

        // 2. Search for product defined in Excel
        ProductsPage productsPage = homePage.clickProducts();
        productsPage.searchProduct(product);
        String productName = productsPage.getFirstProductName();
        LOGGER.info("Found product: {}", productName);

        // 3. Add to cart & proceed
        CartPage cartPage = productsPage.addProductAndProceedToCart();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");

        CheckoutPage checkoutPage = cartPage.proceedToCheckout();
        Assert.assertTrue(checkoutPage.isCheckoutPageVisible(), "Checkout details should be displayed");

        // 4. Enter comment & submit
        checkoutPage.enterComment("Data driven order for " + product);
        PaymentPage paymentPage = checkoutPage.clickPlaceOrder();

        // 5. Submit payment details from Excel
        OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(cardName, cardNumber, cvc, expMonth, expYear);

        // 6. Verify order confirmation
        Assert.assertTrue(confirmationPage.isOrderPlacedDisplayed(), "'ORDER PLACED!' should be visible");
        LOGGER.info("Data-Driven order successfully placed for test case: [{}]", testCase);
    }
}
