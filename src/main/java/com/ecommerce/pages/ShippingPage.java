package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

/**
 * Page Object representing Step 4 (Shipping) of the Checkout process.
 */
public class ShippingPage extends BasePage {

    private final By checkboxTerms = By.id("cgv");
    private final By labelDeliveryPrice = By.cssSelector(".delivery_option_price .price");
    private final By btnProcessCarrier = By.name("processCarrier");

    @Step("Check 'I agree to the terms of service' checkbox")
    public ShippingPage checkTermsOfService() {
        WebElement element = performExplicitWait(checkboxTerms, WaitStrategy.PRESENCE);
        if (!element.isSelected()) {
            click(checkboxTerms, "Terms of Service Checkbox", WaitStrategy.CLICKABLE);
        }
        return this;
    }

    @Step("Get shipping carrier delivery price")
    public String getShippingPrice() {
        return getText(labelDeliveryPrice, "Delivery Price", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Proceed to checkout' from Shipping page")
    public PaymentPage proceedToPayment() {
        scrollToElement(btnProcessCarrier);
        click(btnProcessCarrier, "Proceed Carrier Button", WaitStrategy.CLICKABLE);
        return new PaymentPage();
    }
}
