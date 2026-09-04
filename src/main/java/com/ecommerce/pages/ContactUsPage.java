package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.Alert;
import org.openqa.selenium.By;

import java.io.File;

/**
 * Page Object representing Automation Exercise Contact Us Page (/contact_us).
 */
public class ContactUsPage extends BasePage {

    private final By headingGetInTouch = By.xpath("//h2[contains(text(), 'Get In Touch')]");
    private final By inputName = By.cssSelector("input[data-qa='name']");
    private final By inputEmail = By.cssSelector("input[data-qa='email']");
    private final By inputSubject = By.cssSelector("input[data-qa='subject']");
    private final By txtMessage = By.cssSelector("textarea[data-qa='message']");
    private final By inputFileUpload = By.cssSelector("input[name='upload_file']");
    private final By btnSubmit = By.cssSelector("input[data-qa='submit-button']");
    private final By alertSuccess = By.cssSelector("div.status.alert.alert-success");
    private final By btnHome = By.xpath("//a[contains(@class, 'btn-success') and contains(., 'Home')]");

    @Step("Verify 'GET IN TOUCH' is visible")
    public boolean isGetInTouchVisible() {
        return isDisplayed(headingGetInTouch, "Get In Touch Heading", WaitStrategy.VISIBLE);
    }

    @Step("Enter name: '{name}', email: '{email}', subject: '{subject}', message: '{message}'")
    public ContactUsPage fillContactForm(String name, String email, String subject, String message) {
        sendKeys(inputName, name, "Contact Name", WaitStrategy.VISIBLE);
        sendKeys(inputEmail, email, "Contact Email", WaitStrategy.VISIBLE);
        sendKeys(inputSubject, subject, "Contact Subject", WaitStrategy.VISIBLE);
        sendKeys(txtMessage, message, "Contact Message", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Upload sample file")
    public ContactUsPage uploadFile(String filePath) {
        sendKeys(inputFileUpload, filePath, "File Upload Input", WaitStrategy.PRESENCE);
        return this;
    }

    @Step("Click 'Submit' button and accept alert")
    public ContactUsPage submitFormAndAcceptAlert() {
        dismissAdIfPresent();
        scrollToElement(btnSubmit);
        click(btnSubmit, "Submit Button", WaitStrategy.CLICKABLE);
        try {
            Alert alert = getDriver().switchTo().alert();
            alert.accept();
        } catch (Exception ignored) {
        }
        return this;
    }

    @Step("Verify success message 'Success! Your details have been submitted successfully.' is visible")
    public boolean isSuccessMessageVisible() {
        return isDisplayed(alertSuccess, "Contact Form Success Alert", WaitStrategy.VISIBLE);
    }

    @Step("Get success alert text")
    public String getSuccessAlertText() {
        return getText(alertSuccess, "Contact Form Success Alert", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Home' button and verify landing page")
    public HomePage clickHome() {
        click(btnHome, "Home Button", WaitStrategy.CLICKABLE);
        if (getCurrentUrl().contains("#google_vignette")) {
            getDriver().navigate().to("https://automationexercise.com");
        }
        return new HomePage();
    }
}
