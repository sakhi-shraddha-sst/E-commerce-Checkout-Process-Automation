package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Login / Signup Page (/login).
 */
public class LoginPage extends BasePage {

    private final By headingLogin = By.xpath("//div[@class='login-form']//h2[contains(text(), 'Login to your account')]");
    private final By inputLoginEmail = By.cssSelector("input[data-qa='login-email']");
    private final By inputLoginPassword = By.cssSelector("input[data-qa='login-password']");
    private final By btnLogin = By.cssSelector("button[data-qa='login-button']");
    private final By labelLoginError = By.xpath("//div[@class='login-form']//p");

    private final By headingSignup = By.xpath("//div[@class='signup-form']//h2[contains(text(), 'New User Signup!')]");
    private final By inputSignupName = By.cssSelector("input[data-qa='signup-name']");
    private final By inputSignupEmail = By.cssSelector("input[data-qa='signup-email']");
    private final By btnSignup = By.cssSelector("button[data-qa='signup-button']");

    @Step("Verify 'Login to your account' header is visible")
    public boolean isLoginHeaderVisible() {
        return isDisplayed(headingLogin, "Login to your account header", WaitStrategy.VISIBLE);
    }

    @Step("Enter email: '{email}'")
    public LoginPage enterEmail(String email) {
        sendKeys(inputLoginEmail, email, "Login Email Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Enter password")
    public LoginPage enterPassword(String password) {
        sendKeys(inputLoginPassword, password, "Login Password Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Login' button")
    public HomePage clickLoginButton() {
        dismissAdIfPresent();
        click(btnLogin, "Login Button", WaitStrategy.CLICKABLE);
        return new HomePage();
    }

    @Step("Login with email: '{email}' and password")
    public HomePage login(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        return clickLoginButton();
    }

    @Step("Verify 'New User Signup!' header is visible")
    public boolean isSignupHeaderVisible() {
        return isDisplayed(headingSignup, "New User Signup header", WaitStrategy.VISIBLE);
    }

    @Step("Enter signup name: '{name}' and email: '{email}'")
    public LoginPage enterSignupDetails(String name, String email) {
        sendKeys(inputSignupName, name, "Signup Name Field", WaitStrategy.VISIBLE);
        sendKeys(inputSignupEmail, email, "Signup Email Field", WaitStrategy.VISIBLE);
        return this;
    }

    private final By labelSignupError = By.xpath("//div[@class='signup-form']//p");

    @Step("Click 'Signup' button")
    public SignupPage clickSignupButton() {
        dismissAdIfPresent();
        click(btnSignup, "Signup Button", WaitStrategy.CLICKABLE);
        return new SignupPage();
    }

    @Step("Get login error message")
    public String getLoginErrorMessage() {
        return getText(labelLoginError, "Login Error Message", WaitStrategy.VISIBLE);
    }

    @Step("Get signup error message")
    public String getSignupErrorMessage() {
        return getText(labelSignupError, "Signup Error Message", WaitStrategy.VISIBLE);
    }
}
