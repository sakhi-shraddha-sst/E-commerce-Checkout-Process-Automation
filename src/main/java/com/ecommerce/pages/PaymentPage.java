package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Payment Page (/payment).
 */
public class PaymentPage extends BasePage {

    private final By inputNameOnCard = By.cssSelector("input[data-qa='name-on-card']");
    private final By inputCardNumber = By.cssSelector("input[data-qa='card-number']");
    private final By inputCvc = By.cssSelector("input[data-qa='cvc']");
    private final By inputExpiryMonth = By.cssSelector("input[data-qa='expiry-month']");
    private final By inputExpiryYear = By.cssSelector("input[data-qa='expiry-year']");
    private final By btnPayAndConfirm = By.cssSelector("button[data-qa='pay-button']");

    @Step("Enter Name on Card: '{name}'")
    public PaymentPage enterNameOnCard(String name) {
        sendKeys(inputNameOnCard, name, "Name on Card Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Enter Card Number: '{cardNumber}'")
    public PaymentPage enterCardNumber(String cardNumber) {
        sendKeys(inputCardNumber, cardNumber, "Card Number Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Enter CVC: '{cvc}'")
    public PaymentPage enterCvc(String cvc) {
        sendKeys(inputCvc, cvc, "CVC Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Enter Expiration Month: '{month}'")
    public PaymentPage enterExpiryMonth(String month) {
        sendKeys(inputExpiryMonth, month, "Expiration Month Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Enter Expiration Year: '{year}'")
    public PaymentPage enterExpiryYear(String year) {
        sendKeys(inputExpiryYear, year, "Expiration Year Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Pay and Confirm Order' button")
    public OrderConfirmationPage clickPayAndConfirmOrder() {
        dismissAdIfPresent();
        scrollToElement(btnPayAndConfirm);
        click(btnPayAndConfirm, "Pay and Confirm Order Button", WaitStrategy.CLICKABLE);
        return new OrderConfirmationPage();
    }

    @Step("Enter payment details and submit order")
    public OrderConfirmationPage payAndConfirm(String name, String cardNumber, String cvc, String month, String year) {
        enterNameOnCard(name);
        enterCardNumber(cardNumber);
        enterCvc(cvc);
        enterExpiryMonth(month);
        enterExpiryYear(year);
        return clickPayAndConfirmOrder();
    }
}
