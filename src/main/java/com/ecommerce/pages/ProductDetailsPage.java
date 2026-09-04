package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing the Individual Product Detail view.
 */
public class ProductDetailsPage extends BasePage {

    private final By productNameHeader = By.cssSelector("h1[itemprop='name']");
    private final By inputQuantity = By.id("quantity_wanted");
    private final By selectSize = By.id("group_1");
    private final By btnAddToCart = By.name("Submit");
    private final By btnProceedToCheckout = By.cssSelector("a[title='Proceed to checkout']");

    @Step("Get product title on detail page")
    public String getProductTitle() {
        return getText(productNameHeader, "Product Title", WaitStrategy.VISIBLE);
    }

    @Step("Set product quantity to '{quantity}'")
    public ProductDetailsPage setQuantity(String quantity) {
        sendKeys(inputQuantity, quantity, "Quantity Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Select size '{size}'")
    public ProductDetailsPage selectSize(String size) {
        selectByVisibleText(selectSize, size, "Size Dropdown", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Click Add to Cart button")
    public ProductDetailsPage clickAddToCart() {
        click(btnAddToCart, "Add to Cart Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Proceed to checkout from layer modal")
    public CartPage proceedToCheckout() {
        click(btnProceedToCheckout, "Proceed to Checkout Modal Button", WaitStrategy.CLICKABLE);
        return new CartPage();
    }

    @Step("Add product to cart with quantity '{quantity}' and proceed to checkout")
    public CartPage addProductToCartAndProceed(String quantity) {
        setQuantity(quantity);
        clickAddToCart();
        return proceedToCheckout();
    }
}
