package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Signup & Account Creation/Deletion Pages.
 */
public class SignupPage extends BasePage {

    // Account Information Form
    private final By headingAccountInfo = By.xpath("//b[contains(text(), 'Enter Account Information')]");
    private final By radioGender1 = By.id("id_gender1");
    private final By inputPassword = By.cssSelector("input[data-qa='password']");
    private final By selectDays = By.cssSelector("select[data-qa='days']");
    private final By selectMonths = By.cssSelector("select[data-qa='months']");
    private final By selectYears = By.cssSelector("select[data-qa='years']");
    private final By checkNewsletter = By.id("newsletter");
    private final By checkOptin = By.id("optin");

    // Address Information Form
    private final By inputFirstName = By.cssSelector("input[data-qa='first_name']");
    private final By inputLastName = By.cssSelector("input[data-qa='last_name']");
    private final By inputCompany = By.cssSelector("input[data-qa='company']");
    private final By inputAddress1 = By.cssSelector("input[data-qa='address']");
    private final By inputAddress2 = By.cssSelector("input[data-qa='address2']");
    private final By selectCountry = By.cssSelector("select[data-qa='country']");
    private final By inputState = By.cssSelector("input[data-qa='state']");
    private final By inputCity = By.cssSelector("input[data-qa='city']");
    private final By inputZipcode = By.cssSelector("input[data-qa='zipcode']");
    private final By inputMobileNumber = By.cssSelector("input[data-qa='mobile_number']");
    private final By btnCreateAccount = By.cssSelector("button[data-qa='create-account']");

    // Account Created Confirmation
    private final By headingAccountCreated = By.cssSelector("h2[data-qa='account-created']");
    private final By btnContinue = By.cssSelector("a[data-qa='continue-button']");

    // Account Deleted Confirmation
    private final By headingAccountDeleted = By.cssSelector("h2[data-qa='account-deleted']");

    @Step("Verify that 'ENTER ACCOUNT INFORMATION' is visible")
    public boolean isAccountInfoVisible() {
        return isDisplayed(headingAccountInfo, "Enter Account Information Heading", WaitStrategy.VISIBLE);
    }

    @Step("Fill Account Information: Title, Password, Date of birth")
    public SignupPage fillAccountDetails(String password, String day, String month, String year) {
        click(radioGender1, "Title Mr. Radio", WaitStrategy.CLICKABLE);
        sendKeys(inputPassword, password, "Password Field", WaitStrategy.VISIBLE);
        selectByValue(selectDays, day, "Days Dropdown", WaitStrategy.VISIBLE);
        selectByValue(selectMonths, month, "Months Dropdown", WaitStrategy.VISIBLE);
        selectByValue(selectYears, year, "Years Dropdown", WaitStrategy.VISIBLE);
        scrollToElement(checkNewsletter);
        jsClick(checkNewsletter, "Newsletter Checkbox");
        jsClick(checkOptin, "Special Offers Checkbox");
        return this;
    }

    @Step("Fill Address Details")
    public SignupPage fillAddressDetails(String firstName, String lastName, String company,
                                        String address1, String address2, String country,
                                        String state, String city, String zipcode, String mobileNumber) {
        scrollToElement(inputFirstName);
        sendKeys(inputFirstName, firstName, "First Name Field", WaitStrategy.VISIBLE);
        sendKeys(inputLastName, lastName, "Last Name Field", WaitStrategy.VISIBLE);
        sendKeys(inputCompany, company, "Company Field", WaitStrategy.VISIBLE);
        sendKeys(inputAddress1, address1, "Address Line 1 Field", WaitStrategy.VISIBLE);
        sendKeys(inputAddress2, address2, "Address Line 2 Field", WaitStrategy.VISIBLE);
        selectByValue(selectCountry, country, "Country Dropdown", WaitStrategy.VISIBLE);
        sendKeys(inputState, state, "State Field", WaitStrategy.VISIBLE);
        sendKeys(inputCity, city, "City Field", WaitStrategy.VISIBLE);
        sendKeys(inputZipcode, zipcode, "Zipcode Field", WaitStrategy.VISIBLE);
        sendKeys(inputMobileNumber, mobileNumber, "Mobile Number Field", WaitStrategy.VISIBLE);
        return this;
    }

    @Step("Click 'Create Account' button")
    public SignupPage clickCreateAccount() {
        dismissAdIfPresent();
        scrollToElement(btnCreateAccount);
        click(btnCreateAccount, "Create Account Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify that 'ACCOUNT CREATED!' is visible")
    public boolean isAccountCreatedVisible() {
        return isDisplayed(headingAccountCreated, "ACCOUNT CREATED Heading", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Continue' button after account creation/deletion")
    public HomePage clickContinue() {
        dismissAdIfPresent();
        click(btnContinue, "Continue Button", WaitStrategy.CLICKABLE);
        if (getCurrentUrl().contains("#google_vignette")) {
            getDriver().navigate().to("https://automationexercise.com");
        }
        return new HomePage();
    }

    @Step("Verify that 'ACCOUNT DELETED!' is visible")
    public boolean isAccountDeletedVisible() {
        return isDisplayed(headingAccountDeleted, "ACCOUNT DELETED Heading", WaitStrategy.VISIBLE);
    }
}
