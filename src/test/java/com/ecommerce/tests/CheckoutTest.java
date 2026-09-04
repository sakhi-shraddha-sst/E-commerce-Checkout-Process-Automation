package com.ecommerce.tests;

import com.ecommerce.models.CheckoutModel;
import com.ecommerce.models.UserModel;
import com.ecommerce.pages.*;
import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.TestDataFactory;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Checkout Test Suite covering Official Automation Exercise Scenarios:
 * - Test Case 14: Place Order: Register while Checkout
 * - Test Case 15: Place Order: Register before Checkout
 * - Test Case 16: Place Order: Login before Checkout
 * - Test Case 23: Verify address details in checkout page
 * - Test Case 24: Download Invoice after purchase order
 */
@Epic("Shopping & Checkout")
@Feature("Checkout & Order Placement")
public class CheckoutTest extends BaseTest {

        private final String existingEmail = ConfigReader.get("test_user_email");
        private final String existingPassword = ConfigReader.get("test_user_password");

        @Test(description = "Test Case 14: Place Order: Register while Checkout", priority = 1)
        @Story("Place Order: Register while Checkout")
        @Severity(SeverityLevel.BLOCKER)
        @Description("1. Navigate to url\n2. Add products to cart\n3. Click 'Cart'\n4. Click Proceed To Checkout\n5. Click 'Register / Login'\n6. Fill signup and create account\n7. Verify 'ACCOUNT CREATED!' and click 'Continue'\n8. Verify 'Logged in as username'\n9. Click 'Cart' -> Proceed To Checkout\n10. Enter comment and Place Order\n11. Enter payment details and confirm\n12. Verify 'ORDER PLACED!'\n13. Delete Account")
        public void testCase14_PlaceOrderRegisterWhileCheckout() {
                UserModel user = TestDataFactory.generateRandomUser();
                CheckoutModel payment = TestDataFactory.generatePaymentData();
                LOGGER.info("Starting Test Case 14: Register while Checkout with email: [{}]", user.getEmail());

                HomePage homePage = new HomePage();
                ProductsPage productsPage = homePage.clickProducts();
                CartPage cartPage = productsPage.addProductAndProceedToCart();
                cartPage.proceedToCheckout();

                LoginPage loginPage = cartPage.clickRegisterLoginFromModal();
                loginPage.enterSignupDetails(user.getName(), user.getEmail());
                SignupPage signupPage = loginPage.clickSignupButton();

                signupPage.fillAccountDetails(user.getPassword(), user.getDay(), user.getMonth(), user.getYear());
                signupPage.fillAddressDetails(
                                user.getFirstName(), user.getLastName(), user.getCompany(),
                                user.getAddress(), user.getAddress2(), user.getCountry(),
                                user.getState(), user.getCity(), user.getZipcode(), user.getMobile());
                signupPage.clickCreateAccount();
                Assert.assertTrue(signupPage.isAccountCreatedVisible(), "'ACCOUNT CREATED!' should be visible");

                homePage = signupPage.clickContinue();
                Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

                cartPage = homePage.clickCart();
                CheckoutPage checkoutPage = cartPage.proceedToCheckout();
                checkoutPage.enterComment(payment.getOrderComment());
                PaymentPage paymentPage = checkoutPage.clickPlaceOrder();

                OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(
                                payment.getNameOnCard(), payment.getCardNumber(), payment.getCvc(),
                                payment.getExpiryMonth(), payment.getExpiryYear());
                Assert.assertTrue(confirmationPage.isOrderPlacedDisplayed(), "'ORDER PLACED!' should be visible");

                signupPage = homePage.clickDeleteAccount();
                Assert.assertTrue(signupPage.isAccountDeletedVisible(), "'ACCOUNT DELETED!' should be visible");
                signupPage.clickContinue();

                LOGGER.info("Test Case 14 completed successfully!");
        }

        @Test(description = "Test Case 15: Place Order: Register before Checkout", priority = 2)
        @Story("Place Order: Register before Checkout")
        @Severity(SeverityLevel.BLOCKER)
        @Description("1. Navigate to url\n2. Click 'Signup / Login'\n3. Fill signup and create account\n4. Verify 'ACCOUNT CREATED!' and click 'Continue'\n5. Verify 'Logged in as username'\n6. Add products to cart\n7. Click 'Cart' -> Proceed To Checkout\n8. Enter comment and Place Order\n9. Enter payment details and confirm\n10. Verify 'ORDER PLACED!'\n11. Delete Account")
        public void testCase15_PlaceOrderRegisterBeforeCheckout() {
                UserModel user = TestDataFactory.generateRandomUser();
                CheckoutModel payment = TestDataFactory.generatePaymentData();
                LOGGER.info("Starting Test Case 15: Register before Checkout with email: [{}]", user.getEmail());

                HomePage homePage = new HomePage();
                LoginPage loginPage = homePage.clickSignupLogin();
                loginPage.enterSignupDetails(user.getName(), user.getEmail());
                SignupPage signupPage = loginPage.clickSignupButton();

                signupPage.fillAccountDetails(user.getPassword(), user.getDay(), user.getMonth(), user.getYear());
                signupPage.fillAddressDetails(
                                user.getFirstName(), user.getLastName(), user.getCompany(),
                                user.getAddress(), user.getAddress2(), user.getCountry(),
                                user.getState(), user.getCity(), user.getZipcode(), user.getMobile());
                signupPage.clickCreateAccount();
                homePage = signupPage.clickContinue();
                Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

                ProductsPage productsPage = homePage.clickProducts();
                CartPage cartPage = productsPage.addProductAndProceedToCart();
                CheckoutPage checkoutPage = cartPage.proceedToCheckout();

                checkoutPage.enterComment(payment.getOrderComment());
                PaymentPage paymentPage = checkoutPage.clickPlaceOrder();

                OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(
                                payment.getNameOnCard(), payment.getCardNumber(), payment.getCvc(),
                                payment.getExpiryMonth(), payment.getExpiryYear());
                Assert.assertTrue(confirmationPage.isOrderPlacedDisplayed(), "'ORDER PLACED!' should be visible");

                signupPage = homePage.clickDeleteAccount();
                Assert.assertTrue(signupPage.isAccountDeletedVisible(), "'ACCOUNT DELETED!' should be visible");
                signupPage.clickContinue();

                LOGGER.info("Test Case 15 completed successfully!");
        }

        @Test(description = "Test Case 16: Place Order: Login before Checkout", priority = 3)
        @Story("Place Order: Login before Checkout")
        @Severity(SeverityLevel.BLOCKER)
        @Description("1. Navigate to url\n2. Click 'Signup / Login' and authenticate\n3. Verify 'Logged in as username'\n4. Add products to cart\n5. Click 'Cart' -> Proceed To Checkout\n6. Verify Address Details and enter comment\n7. Place Order -> Enter payment details and confirm\n8. Verify 'ORDER PLACED!' and confirmation message")
        public void testCase16_PlaceOrderLoginBeforeCheckout() {
                LOGGER.info("Starting Test Case 16: Place Order: Login before Checkout");
                CheckoutModel payment = TestDataFactory.generatePaymentData();

                HomePage homePage = new HomePage();
                LoginPage loginPage = homePage.clickSignupLogin();
                homePage = loginPage.login(existingEmail, existingPassword);
                Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

                ProductsPage productsPage = homePage.clickProducts();
                productsPage.searchProduct("Dress");
                CartPage cartPage = productsPage.addProductAndProceedToCart();

                CheckoutPage checkoutPage = cartPage.proceedToCheckout();
                Assert.assertTrue(checkoutPage.isCheckoutPageVisible(), "Checkout address details should be displayed");

                checkoutPage.enterComment(payment.getOrderComment());
                PaymentPage paymentPage = checkoutPage.clickPlaceOrder();

                OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(
                                payment.getNameOnCard(), payment.getCardNumber(), payment.getCvc(),
                                payment.getExpiryMonth(), payment.getExpiryYear());

                Assert.assertTrue(confirmationPage.isOrderPlacedDisplayed(), "'ORDER PLACED!' should be displayed");
                String successMsg = confirmationPage.getOrderSuccessMessage();
                LOGGER.info("Confirmation Message: {}", successMsg);
                Assert.assertTrue(successMsg.contains("Congratulations! Your order has been confirmed!"),
                                "Confirmation message should confirm order placement");

                Assert.assertTrue(confirmationPage.isDownloadInvoiceDisplayed(),
                                "Download Invoice button should be visible");
                confirmationPage.clickContinue();

                LOGGER.info("Test Case 16 completed successfully!");
        }

        @Test(description = "Test Case 23: Verify address details in checkout page", priority = 4)
        @Story("Address Details Consistency Verification")
        @Severity(SeverityLevel.CRITICAL)
        @Description("1. Navigate to url\n2. Click 'Signup / Login' and create account with specific address\n3. Add product to cart\n4. Proceed to checkout\n5. Verify delivery and billing address match registered address\n6. Delete account")
        public void testCase23_VerifyAddressDetailsInCheckoutPage() {
                UserModel user = TestDataFactory.generateRandomUser();
                LOGGER.info("Starting Test Case 23: Verify address details with email: [{}]", user.getEmail());

                HomePage homePage = new HomePage();
                LoginPage loginPage = homePage.clickSignupLogin();
                loginPage.enterSignupDetails(user.getName(), user.getEmail());
                SignupPage signupPage = loginPage.clickSignupButton();

                signupPage.fillAccountDetails(user.getPassword(), user.getDay(), user.getMonth(), user.getYear());
                signupPage.fillAddressDetails(
                                user.getFirstName(), user.getLastName(), user.getCompany(),
                                user.getAddress(), user.getAddress2(), user.getCountry(),
                                user.getState(), user.getCity(), user.getZipcode(), user.getMobile());
                signupPage.clickCreateAccount();
                homePage = signupPage.clickContinue();

                ProductsPage productsPage = homePage.clickProducts();
                CartPage cartPage = productsPage.addProductAndProceedToCart();
                CheckoutPage checkoutPage = cartPage.proceedToCheckout();

                String deliveryText = checkoutPage.getDeliveryAddressText();
                String billingText = checkoutPage.getBillingAddressText();
                LOGGER.info("Delivery Address:\n{}", deliveryText);

                Assert.assertTrue(deliveryText.contains(user.getAddress()),
                                "Delivery address should contain registered street address");
                Assert.assertTrue(deliveryText.contains(user.getCity()),
                                "Delivery address should contain registered city");
                Assert.assertTrue(billingText.contains(user.getAddress()),
                                "Billing address should contain registered street address");

                signupPage = homePage.clickDeleteAccount();
                signupPage.clickContinue();

                LOGGER.info("Test Case 23 completed successfully!");
        }

        @Test(description = "Test Case 24: Download Invoice after purchase order", priority = 5)
        @Story("Download Purchase Invoice")
        @Severity(SeverityLevel.NORMAL)
        @Description("1. Navigate to url\n2. Add products to cart\n3. Login & Proceed to checkout\n4. Place order and confirm payment\n5. Verify 'ORDER PLACED!'\n6. Verify 'Download Invoice' button is present\n7. Click 'Continue'")
        public void testCase24_DownloadInvoiceAfterPurchaseOrder() {
                LOGGER.info("Starting Test Case 24: Download Invoice after purchase order");
                CheckoutModel payment = TestDataFactory.generatePaymentData();

                HomePage homePage = new HomePage();
                LoginPage loginPage = homePage.clickSignupLogin();
                homePage = loginPage.login(existingEmail, existingPassword);

                ProductsPage productsPage = homePage.clickProducts();
                CartPage cartPage = productsPage.addProductAndProceedToCart();
                CheckoutPage checkoutPage = cartPage.proceedToCheckout();

                checkoutPage.enterComment(payment.getOrderComment());
                PaymentPage paymentPage = checkoutPage.clickPlaceOrder();
                OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(
                                payment.getNameOnCard(), payment.getCardNumber(), payment.getCvc(),
                                payment.getExpiryMonth(), payment.getExpiryYear());

                Assert.assertTrue(confirmationPage.isOrderPlacedDisplayed(), "'ORDER PLACED!' should be visible");
                Assert.assertTrue(confirmationPage.isDownloadInvoiceDisplayed(),
                                "Download Invoice button should be displayed");

                homePage = confirmationPage.clickContinue();
                Assert.assertTrue(homePage.isHomePageVisible(), "Returned to home page successfully");

                LOGGER.info("Test Case 24 completed successfully!");
        }
}
