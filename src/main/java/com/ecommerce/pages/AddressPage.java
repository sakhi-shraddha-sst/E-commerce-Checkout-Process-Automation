package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Step 3 (Address) of the Checkout process.
 */
public class AddressPage extends BasePage {

    private final By deliveryAddressBox = By.id("address_delivery");
    private final By deliveryFullName = By.cssSelector("#address_delivery .address_firstname");
    private final By deliveryAddress1 = By.cssSelector("#address_delivery .address_address1");
    private final By txtComment = By.name("message");
    private final By btnProcessAddress = By.name("processAddress");

    @Step("Get delivery address full text")
    public String getDeliveryAddressText() {
        return getText(deliveryAddressBox, "Delivery Address Box", WaitStrategy.VISIBLE);
    }

    @Step("Enter comment about the order: '{comment}'")
    public AddressPage enterOrderComment(String comment) {
        sendKeys(txtComment, comment, "Order Comment Box", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Proceed to checkout' from Address page")
    public ShippingPage proceedToShipping() {
        scrollToElement(btnProcessAddress);
        click(btnProcessAddress, "Proceed Address Button", WaitStrategy.CLICKABLE);
        return new ShippingPage();
    }
}
