package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Order Placed Confirmation Page (/payment_done).
 */
public class OrderConfirmationPage extends BasePage {

    private final By headingOrderPlaced = By.cssSelector("h2[data-qa='order-placed']");
    private final By txtOrderSuccess = By.xpath("//p[contains(text(), 'Congratulations! Your order has been confirmed!')]");
    private final By btnDownloadInvoice = By.cssSelector("a.check_out[href*='/download_invoice/']");
    private final By btnContinue = By.cssSelector("a[data-qa='continue-button']");

    @Step("Verify that 'ORDER PLACED!' is visible")
    public boolean isOrderPlacedDisplayed() {
        return isDisplayed(headingOrderPlaced, "ORDER PLACED Heading", WaitStrategy.VISIBLE);
    }

    @Step("Get order placed heading text")
    public String getOrderPlacedHeading() {
        return getText(headingOrderPlaced, "ORDER PLACED Heading", WaitStrategy.VISIBLE);
    }

    @Step("Verify success message 'Your order has been placed successfully!' / 'Congratulations! Your order has been confirmed!'")
    public String getOrderSuccessMessage() {
        return getText(txtOrderSuccess, "Order Success Message", WaitStrategy.VISIBLE);
    }

    @Step("Verify 'Download Invoice' button is present")
    public boolean isDownloadInvoiceDisplayed() {
        return isDisplayed(btnDownloadInvoice, "Download Invoice Button", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Continue' button to return to homepage")
    public HomePage clickContinue() {
        dismissAdIfPresent();
        click(btnContinue, "Continue Button", WaitStrategy.CLICKABLE);
        return new HomePage();
    }
}
