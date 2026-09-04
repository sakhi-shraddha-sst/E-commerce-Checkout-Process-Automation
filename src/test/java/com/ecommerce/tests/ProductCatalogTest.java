package com.ecommerce.tests;

import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.ProductDetailPage;
import com.ecommerce.pages.ProductsPage;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Product Catalog Test Suite covering Official Automation Exercise Scenarios:
 * - Test Case 8: Verify All Products and product detail page
 * - Test Case 9: Search Product
 * - Test Case 18: View Category Products
 * - Test Case 19: View & Cart Brand Products
 * - Test Case 21: Add review on product
 */
@Epic("Automation Exercise Platform")
@Feature("Product Catalog & Discovery")
public class ProductCatalogTest extends BaseTest {

    @Test(description = "Test Case 8: Verify All Products and product detail page", priority = 1)
    @Story("Product Detail Page Verification")
    @Severity(SeverityLevel.CRITICAL)
    @Description("1. Navigate to url\n2. Click 'Products'\n3. Verify ALL PRODUCTS page\n4. Click 'View Product' of first product\n5. Verify product details: name, category, price, availability, condition, brand")
    public void testCase08_VerifyAllProductsAndDetailPage() {
        LOGGER.info("Starting Test Case 8: Verify All Products and product detail page");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "ALL PRODUCTS page should be visible");

        ProductDetailPage detailPage = productsPage.clickViewProductFirst();
        Assert.assertTrue(detailPage.isProductNameVisible(), "Product name should be visible");
        Assert.assertFalse(detailPage.getProductPrice().isEmpty(), "Product price should not be empty");
        Assert.assertTrue(detailPage.isAvailabilityVisible(), "Product availability should be visible");
        Assert.assertTrue(detailPage.isConditionVisible(), "Product condition should be visible");
        Assert.assertTrue(detailPage.isBrandVisible(), "Product brand should be visible");

        LOGGER.info("Product: [{}] | Category: [{}] | Price: [{}]",
                detailPage.getProductName(), detailPage.getProductCategory(), detailPage.getProductPrice());
        LOGGER.info("Test Case 8 completed successfully!");
    }

    @Test(description = "Test Case 9: Search Product", priority = 2)
    @Story("Product Search Functionality")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Products'\n3. Verify ALL PRODUCTS page\n4. Enter product name and click search\n5. Verify SEARCHED PRODUCTS and product results")
    public void testCase09_SearchProduct() {
        LOGGER.info("Starting Test Case 9: Search Product");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        Assert.assertTrue(productsPage.isProductsPageVisible(), "ALL PRODUCTS page should be visible");

        productsPage.searchProduct("Dress");
        String firstProduct = productsPage.getFirstProductName();
        LOGGER.info("First product found: {}", firstProduct);
        Assert.assertTrue(firstProduct.toLowerCase().contains("dress"),
                "Searched product name should contain 'Dress'");

        LOGGER.info("Test Case 9 completed successfully!");
    }

    @Test(description = "Test Case 18: View Category Products", priority = 3)
    @Story("Category Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click on 'Women' category\n3. Click 'Dress'\n4. Verify category page\n5. Click 'Men' category -> 'Tshirts'\n6. Verify category page")
    public void testCase18_ViewCategoryProducts() {
        LOGGER.info("Starting Test Case 18: View Category Products");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickWomenDressCategory();
        String heading = productsPage.getPageHeading();
        LOGGER.info("Category Heading: {}", heading);
        Assert.assertTrue(heading.toUpperCase().contains("WOMEN"), "Category heading should indicate Women products");

        productsPage = homePage.clickMenTshirtsCategory();
        heading = productsPage.getPageHeading();
        LOGGER.info("Men Category Heading: {}", heading);
        Assert.assertTrue(heading.toUpperCase().contains("MEN"), "Category heading should indicate Men products");

        LOGGER.info("Test Case 18 completed successfully!");
    }

    @Test(description = "Test Case 19: View & Cart Brand Products", priority = 4)
    @Story("Brand Navigation")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Products'\n3. Click 'Polo' brand\n4. Verify brand page\n5. Click 'Madame' brand\n6. Verify brand page")
    public void testCase19_ViewAndCartBrandProducts() {
        LOGGER.info("Starting Test Case 19: View & Cart Brand Products");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();

        productsPage.clickBrandPolo();
        Assert.assertTrue(productsPage.isBrandTitleVisible(), "Brand heading should be visible for Polo");
        LOGGER.info("Polo Brand Heading: {}", productsPage.getPageHeading());

        productsPage.clickBrandMadame();
        Assert.assertTrue(productsPage.isBrandTitleVisible(), "Brand heading should be visible for Madame");
        LOGGER.info("Madame Brand Heading: {}", productsPage.getPageHeading());

        LOGGER.info("Test Case 19 completed successfully!");
    }

    @Test(description = "Test Case 21: Add review on product", priority = 5)
    @Story("Product Customer Reviews")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Products'\n3. Click 'View Product'\n4. Enter name, email and review\n5. Click 'Submit'\n6. Verify 'Thank you for your review.'")
    public void testCase21_AddReviewOnProduct() {
        LOGGER.info("Starting Test Case 21: Add review on product");

        HomePage homePage = new HomePage();
        ProductsPage productsPage = homePage.clickProducts();
        ProductDetailPage detailPage = productsPage.clickViewProductFirst();

        com.ecommerce.models.UserModel reviewer = com.ecommerce.utils.TestDataFactory.generateRandomUser();
        detailPage.submitReview(reviewer.getName(), reviewer.getEmail(), "Excellent fabric quality and quick delivery!");
        Assert.assertTrue(detailPage.isReviewSuccessVisible(), "Review success message should be visible");

        LOGGER.info("Test Case 21 completed successfully!");
    }
}
