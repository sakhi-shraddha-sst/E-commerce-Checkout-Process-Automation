package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Product Detail Page (/product_details/*).
 */
public class ProductDetailPage extends BasePage {

    private final By headingProductName = By.cssSelector(".product-information h2");
    private final By labelCategory = By.xpath("//div[@class='product-information']//p[contains(text(), 'Category')]");
    private final By labelPrice = By.xpath("//div[@class='product-information']//span/span");
    private final By inputQuantity = By.id("quantity");
    private final By btnAddToCart = By.xpath("//div[@class='product-information']//button[contains(@class, 'cart')]");
    private final By labelAvailability = By.xpath("//div[@class='product-information']//p[b[contains(text(),'Availability')]]");
    private final By labelCondition = By.xpath("//div[@class='product-information']//p[b[contains(text(),'Condition')]]");
    private final By labelBrand = By.xpath("//div[@class='product-information']//p[b[contains(text(),'Brand')]]");

    // Review Form
    private final By inputReviewName = By.id("name");
    private final By inputReviewEmail = By.id("email");
    private final By txtReview = By.id("review");
    private final By btnSubmitReview = By.id("button-review");
    private final By msgReviewSuccess = By.xpath("//span[contains(text(),'Thank you for your review.')]");

    private final By linkViewCartModal = By.cssSelector("#cartModal a[href='/view_cart']");

    @Step("Verify product name is displayed on detail page")
    public boolean isProductNameVisible() {
        return isDisplayed(headingProductName, "Product Name", WaitStrategy.VISIBLE);
    }

    @Step("Get product name")
    public String getProductName() {
        return getText(headingProductName, "Product Name", WaitStrategy.VISIBLE);
    }

    @Step("Get product category")
    public String getProductCategory() {
        return getText(labelCategory, "Product Category", WaitStrategy.VISIBLE);
    }

    @Step("Get product price")
    public String getProductPrice() {
        return getText(labelPrice, "Product Price", WaitStrategy.VISIBLE);
    }

    @Step("Verify availability label is displayed")
    public boolean isAvailabilityVisible() {
        return isDisplayed(labelAvailability, "Availability Label", WaitStrategy.VISIBLE);
    }

    @Step("Verify condition label is displayed")
    public boolean isConditionVisible() {
        return isDisplayed(labelCondition, "Condition Label", WaitStrategy.VISIBLE);
    }

    @Step("Verify brand label is displayed")
    public boolean isBrandVisible() {
        return isDisplayed(labelBrand, "Brand Label", WaitStrategy.VISIBLE);
    }

    @Step("Set product quantity to '{quantity}'")
    public ProductDetailPage setQuantity(String quantity) {
        sendKeys(inputQuantity, quantity, "Quantity Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Add to cart' button")
    public ProductDetailPage clickAddToCart() {
        dismissAdIfPresent();
        click(btnAddToCart, "Add to Cart Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Click 'View Cart' link in modal")
    public CartPage clickViewCart() {
        click(linkViewCartModal, "View Cart Modal Link", WaitStrategy.CLICKABLE);
        return new CartPage();
    }

    @Step("Submit review: name='{name}', email='{email}', review='{review}'")
    public ProductDetailPage submitReview(String name, String email, String review) {
        scrollToElement(inputReviewName);
        sendKeys(inputReviewName, name, "Reviewer Name", WaitStrategy.VISIBLE);
        sendKeys(inputReviewEmail, email, "Reviewer Email", WaitStrategy.VISIBLE);
        sendKeys(txtReview, review, "Review Text", WaitStrategy.VISIBLE);
        click(btnSubmitReview, "Submit Review Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify review success message is displayed")
    public boolean isReviewSuccessVisible() {
        return isDisplayed(msgReviewSuccess, "Review Success Message", WaitStrategy.VISIBLE);
    }
}
