package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Checkout Page (/checkout).
 */
public class CheckoutPage extends BasePage {

    private final By deliveryAddressBox = By.id("address_delivery");
    private final By billingAddressBox = By.id("address_invoice");
    private final By txtComment = By.cssSelector("textarea[name='message']");
    private final By btnPlaceOrder = By.cssSelector("a[href='/payment']");

    @Step("Verify Address Details and Review Order are displayed")
    public boolean isCheckoutPageVisible() {
        return isDisplayed(deliveryAddressBox, "Delivery Address Box", WaitStrategy.VISIBLE);
    }

    @Step("Get delivery address details")
    public String getDeliveryAddressText() {
        return getText(deliveryAddressBox, "Delivery Address Box", WaitStrategy.VISIBLE);
    }

    @Step("Get billing address details")
    public String getBillingAddressText() {
        return getText(billingAddressBox, "Billing Address Box", WaitStrategy.VISIBLE);
    }

    @Step("Enter description in comment text area: '{comment}'")
    public CheckoutPage enterComment(String comment) {
        scrollToElement(txtComment);
        sendKeys(txtComment, comment, "Order Comment Area", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Place Order' button")
    public PaymentPage clickPlaceOrder() {
        dismissAdIfPresent();
        scrollToElement(btnPlaceOrder);
        click(btnPlaceOrder, "Place Order Button", WaitStrategy.CLICKABLE);
        return new PaymentPage();
    }
}
