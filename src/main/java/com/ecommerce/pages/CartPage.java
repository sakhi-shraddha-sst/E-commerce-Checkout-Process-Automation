package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object representing Automation Exercise Shopping Cart Page (/view_cart).
 */
public class CartPage extends BasePage {

    private final By breadcrumbCart = By.xpath("//li[@class='active' and contains(text(), 'Shopping Cart')]");
    private final By tableCart = By.id("cart_info_table");
    private final By cartRows = By.cssSelector("#cart_info_table tbody tr");
    private final By firstProductName = By.cssSelector("td.cart_description h4 a");
    private final By firstProductPrice = By.cssSelector("td.cart_price p");
    private final By firstProductQuantity = By.cssSelector("td.cart_quantity button");
    private final By firstProductTotal = By.cssSelector("td.cart_total p.cart_total_price");
    private final By btnDeleteFirst = By.cssSelector("a.cart_quantity_delete");
    private final By spanEmptyCart = By.id("empty_cart");
    private final By btnProceedToCheckout = By.cssSelector("a.check_out");
    private final By linkRegisterLoginModal = By.xpath("//div[@id='checkoutModal']//a[contains(@href,'/login') or contains(.,'Register')]");

    // Subscription
    private final By headingSubscription = By.xpath("//div[@class='single-widget']//h2[contains(text(),'Subscription')]");
    private final By inputSubscribeEmail = By.id("susbscribe_email");
    private final By btnSubscribe = By.id("subscribe");
    private final By alertSubscribeSuccess = By.cssSelector("#success-subscribe .alert-success");

    @Step("Verify that cart page is displayed")
    public boolean isCartPageVisible() {
        return isDisplayed(breadcrumbCart, "Shopping Cart Breadcrumb", WaitStrategy.VISIBLE);
    }

    @Step("Get product description of first item in cart")
    public String getFirstProductDescription() {
        return getText(firstProductName, "First Cart Item Name", WaitStrategy.VISIBLE);
    }

    @Step("Get price of first item in cart")
    public String getFirstProductPrice() {
        return getText(firstProductPrice, "First Cart Item Price", WaitStrategy.VISIBLE);
    }

    @Step("Get total price of first item in cart")
    public String getFirstProductTotal() {
        return getText(firstProductTotal, "First Cart Item Total", WaitStrategy.VISIBLE);
    }

    @Step("Get quantity of first item in cart")
    public String getFirstProductQuantity() {
        return getText(firstProductQuantity, "First Cart Item Quantity", WaitStrategy.VISIBLE);
    }

    @Step("Get number of distinct products in cart table")
    public int getCartItemsCount() {
        List<WebElement> rows = findElements(cartRows);
        return rows.size();
    }

    @Step("Click 'Delete' (X) button for first cart item")
    public CartPage removeFirstProduct() {
        click(btnDeleteFirst, "Delete Item Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify that cart is empty or product is removed")
    public boolean isCartEmptyOrProductRemoved() {
        try {
            return isDisplayed(spanEmptyCart, "Empty Cart Notice", WaitStrategy.VISIBLE);
        } catch (Exception e) {
            return getCartItemsCount() == 0;
        }
    }

    @Step("Click 'Proceed To Checkout'")
    public CheckoutPage proceedToCheckout() {
        dismissAdIfPresent();
        scrollToElement(btnProceedToCheckout);
        click(btnProceedToCheckout, "Proceed To Checkout Button", WaitStrategy.CLICKABLE);
        return new CheckoutPage();
    }

    @Step("Click 'Register / Login' from checkout modal")
    public LoginPage clickRegisterLoginFromModal() {
        click(linkRegisterLoginModal, "Register / Login modal link", WaitStrategy.CLICKABLE);
        return new LoginPage();
    }

    @Step("Subscribe to newsletter in cart footer with email: '{email}'")
    public CartPage subscribe(String email) {
        scrollToElement(headingSubscription);
        sendKeys(inputSubscribeEmail, email, "Subscription Email Field", WaitStrategy.VISIBLE);
        click(btnSubscribe, "Subscribe Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify cart subscription success message is visible")
    public boolean isSubscriptionSuccessVisible() {
        return isDisplayed(alertSubscribeSuccess, "Subscription Success Alert", WaitStrategy.VISIBLE);
    }
}
