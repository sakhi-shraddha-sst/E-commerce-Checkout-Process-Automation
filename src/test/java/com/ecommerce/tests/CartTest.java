package com.ecommerce.tests;

import com.ecommerce.pages.*;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Cart Test Suite covering Official Automation Exercise Scenarios:
 * - Test Case 12: Add Products in Cart
 * - Test Case 13: Verify Product quantity in Cart
 * - Test Case 17: Remove Products From Cart
 * - Test Case 20: Search Products and Verify Cart After Login
 * - Test Case 22: Add to cart from Recommended items
 */
@Epic("Automation Exercise Platform")
@Feature("Shopping Cart & Item Management")
public class CartTest extends BaseTest {

    private final String testEmail = com.ecommerce.utils.ConfigReader.get("test_user_email");
    private final String testPassword = com.ecommerce.utils.ConfigReader.get("test_user_password");

    @Test(description = "Test Case 12: Add Products in Cart", priority = 1)
    @Story("Add Multiple Products to Cart")
    @Severity(SeverityLevel.CRITICAL)
    @Description("1. Navigate to url\n2. Click 'Products'\n3. Add first product and click 'Continue Shopping'\n4. Add second product and click 'View Cart'\n5. Verify both products in cart with prices and total")
    public void testCase12_AddProductsInCart() {
        LOGGER.info("Starting Test Case 12: Add Products in Cart");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();

        productsPage.addFirstProductToCart();
        productsPage.clickContinueShopping();

        productsPage.addSecondProductToCart();
        CartPage cartPage = productsPage.clickProceedToCartFromModal();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");
        int count = cartPage.getCartItemsCount();
        LOGGER.info("Cart items count: {}", count);
        Assert.assertTrue(count >= 2, "Cart should contain at least 2 distinct products");

        LOGGER.info("Test Case 12 completed successfully!");
    }

    @Test(description = "Test Case 13: Verify Product quantity in Cart", priority = 2)
    @Story("Cart Product Quantity")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'View Product' of first product\n3. Increase quantity to 4\n4. Click 'Add to cart'\n5. Click 'View Cart'\n6. Verify product quantity is 4")
    public void testCase13_VerifyProductQuantityInCart() {
        LOGGER.info("Starting Test Case 13: Verify Product quantity in Cart");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        ProductDetailPage detailPage = productsPage.clickViewProductFirst();

        detailPage.setQuantity("4");
        detailPage.clickAddToCart();
        CartPage cartPage = detailPage.clickViewCart();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");
        String qty = cartPage.getFirstProductQuantity();
        LOGGER.info("Quantity in cart: {}", qty);
        Assert.assertEquals(qty, "4", "Product quantity in cart should be 4");

        LOGGER.info("Test Case 13 completed successfully!");
    }

    @Test(description = "Test Case 17: Remove Products From Cart", priority = 3)
    @Story("Remove Items from Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Add products to cart\n3. Click 'Cart'\n4. Click 'X' button corresponding to product\n5. Verify product is removed")
    public void testCase17_RemoveProductsFromCart() {
        LOGGER.info("Starting Test Case 17: Remove Products From Cart");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        CartPage cartPage = productsPage.addProductAndProceedToCart();

        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");
        cartPage.removeFirstProduct();

        // Brief wait for row removal
        try {
            Thread.sleep(1500);
        } catch (InterruptedException ignored) {
        }

        Assert.assertTrue(cartPage.isCartEmptyOrProductRemoved(), "Cart should be empty or item removed");
        LOGGER.info("Test Case 17 completed successfully!");
    }

    @Test(description = "Test Case 20: Search Products and Verify Cart After Login", priority = 4)
    @Story("Cart Persistence Across User Login")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Search products\n3. Add products to cart\n4. Click 'Cart'\n5. Click 'Signup / Login' and authenticate\n6. Return to Cart and verify products persist")
    public void testCase20_SearchProductsAndVerifyCartAfterLogin() {
        LOGGER.info("Starting Test Case 20: Search Products and Verify Cart After Login");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        productsPage.searchProduct("Dress");
        CartPage cartPage = productsPage.addProductAndProceedToCart();
        String itemBeforeLogin = cartPage.getFirstProductDescription();

        LoginPage loginPage = homePage.clickSignupLogin();
        homePage = loginPage.login(testEmail, testPassword);
        Assert.assertTrue(homePage.isLoggedInAsVisible(), "User should be logged in");

        cartPage = homePage.clickCart();
        String itemAfterLogin = cartPage.getFirstProductDescription();
        LOGGER.info("Cart Item before login: [{}] | after login: [{}]", itemBeforeLogin, itemAfterLogin);
        Assert.assertEquals(itemAfterLogin, itemBeforeLogin, "Product in cart should persist after login");

        LOGGER.info("Test Case 20 completed successfully!");
    }

    @Test(description = "Test Case 22: Add to cart from Recommended items", priority = 5)
    @Story("Recommended Items Carousel Add to Cart")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Scroll to bottom of page\n3. Verify 'RECOMMENDED ITEMS' are visible\n4. Click 'Add To Cart' on recommended item\n5. Click 'View Cart'\n6. Verify product is displayed in cart")
    public void testCase22_AddToCartFromRecommendedItems() {
        LOGGER.info("Starting Test Case 22: Add to cart from Recommended items");

        HomePage homePage = new HomePage();
        homePage.scrollToRecommendedItems();
        Assert.assertTrue(homePage.isRecommendedItemsVisible(), "'RECOMMENDED ITEMS' should be visible");

        CartPage cartPage = homePage.addRecommendedItemToCart();
        Assert.assertTrue(cartPage.isCartPageVisible(), "Cart page should be displayed");
        Assert.assertFalse(cartPage.getFirstProductDescription().isEmpty(), "Cart should contain recommended product");

        LOGGER.info("Test Case 22 completed successfully!");
    }
}
