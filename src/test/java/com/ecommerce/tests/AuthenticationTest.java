package com.ecommerce.tests;

import com.ecommerce.models.UserModel;
import com.ecommerce.pages.HomePage;
import com.ecommerce.pages.LoginPage;
import com.ecommerce.pages.SignupPage;
import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.TestDataFactory;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * Validates authentication flows including User Registration, Valid Login,
 * Invalid Login, Logout, and Duplicate Registration prevention.
 */
@Epic("User Management & Access")
@Feature("Authentication Workflows")
public class AuthenticationTest extends BaseTest {

    private final String existingEmail = ConfigReader.get("test_user_email");
    private final String existingPassword = ConfigReader.get("test_user_password");

    @Test(description = "Test Case 1: Register User", priority = 1)
    @Story("User Registration & Deletion Flow")
    @Severity(SeverityLevel.BLOCKER)
    @Description("1. Navigate to url\n2. Verify home page\n3. Click 'Signup / Login'\n4. Verify 'New User Signup!'\n5. Enter name and unique email\n6. Click 'Signup'\n7. Verify 'ENTER ACCOUNT INFORMATION'\n8. Fill account & address details\n9. Click 'Create Account'\n10. Verify 'ACCOUNT CREATED!'\n11. Click 'Continue'\n12. Verify 'Logged in as username'\n13. Click 'Delete Account'\n14. Verify 'ACCOUNT DELETED!' and click 'Continue'")
    public void testCase01_RegisterUser() {
        UserModel randomUser = TestDataFactory.generateRandomUser();
        LOGGER.info("Starting Test Case 1: Register User dynamically with email: [{}]", randomUser.getEmail());

        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        LoginPage loginPage = homePage.clickSignupLogin();
        Assert.assertTrue(loginPage.isSignupHeaderVisible(), "'New User Signup!' should be visible");

        loginPage.enterSignupDetails(randomUser.getName(), randomUser.getEmail());
        SignupPage signupPage = loginPage.clickSignupButton();

        Assert.assertTrue(signupPage.isAccountInfoVisible(), "'ENTER ACCOUNT INFORMATION' should be visible");

        signupPage.fillAccountDetails(randomUser.getPassword(), randomUser.getDay(), randomUser.getMonth(), randomUser.getYear());
        signupPage.fillAddressDetails(
                randomUser.getFirstName(), randomUser.getLastName(), randomUser.getCompany(),
                randomUser.getAddress(), randomUser.getAddress2(), randomUser.getCountry(),
                randomUser.getState(), randomUser.getCity(), randomUser.getZipcode(), randomUser.getMobile()
        );
        signupPage.clickCreateAccount();

        Assert.assertTrue(signupPage.isAccountCreatedVisible(), "'ACCOUNT CREATED!' should be visible");
        homePage = signupPage.clickContinue();

        Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

        signupPage = homePage.clickDeleteAccount();
        Assert.assertTrue(signupPage.isAccountDeletedVisible(), "'ACCOUNT DELETED!' should be visible");
        homePage = signupPage.clickContinue();

        LOGGER.info("Test Case 1 completed successfully!");
    }

    @Test(description = "Test Case 2: Login User with correct email and password", priority = 2)
    @Story("Valid Login and Logout")
    @Severity(SeverityLevel.CRITICAL)
    @Description("1. Navigate to url\n2. Click 'Signup / Login'\n3. Verify 'Login to your account'\n4. Enter correct email and password\n5. Click 'Login'\n6. Verify 'Logged in as username'\n7. Click 'Logout'\n8. Verify login page is visible")
    public void testCase02_LoginUserWithCorrectCredentials() {
        LOGGER.info("Starting Test Case 2: Login User with correct credentials");

        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        LoginPage loginPage = homePage.clickSignupLogin();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "'Login to your account' should be visible");

        homePage = loginPage.login(existingEmail, existingPassword);
        Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

        loginPage = homePage.clickLogout();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "User should be logged out and login header visible");

        LOGGER.info("Test Case 2 completed successfully!");
    }

    @Test(description = "Test Case 3: Login User with incorrect email and password", priority = 3)
    @Story("Invalid Login Handling")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Signup / Login'\n3. Verify 'Login to your account'\n4. Enter incorrect email and password\n5. Click 'Login'\n6. Verify error 'Your email or password is incorrect!'")
    public void testCase03_LoginUserWithIncorrectCredentials() {
        LOGGER.info("Starting Test Case 3: Login User with incorrect credentials");

        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        LoginPage loginPage = homePage.clickSignupLogin();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "'Login to your account' should be visible");

        UserModel invalidUser = TestDataFactory.generateRandomUser();
        loginPage.enterEmail(invalidUser.getEmail());
        loginPage.enterPassword(invalidUser.getPassword());
        loginPage.clickLoginButton();

        String errorMsg = loginPage.getLoginErrorMessage();
        LOGGER.info("Observed error: {}", errorMsg);
        Assert.assertTrue(errorMsg.toLowerCase().contains("incorrect"),
                "Error message should indicate incorrect credentials");

        LOGGER.info("Test Case 3 completed successfully!");
    }

    @Test(description = "Test Case 4: Logout User", priority = 4)
    @Story("User Logout Flow")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Signup / Login'\n3. Fill email and password\n4. Click 'Login'\n5. Verify 'Logged in as username'\n6. Click 'Logout'\n7. Verify navigated to login page")
    public void testCase04_LogoutUser() {
        LOGGER.info("Starting Test Case 4: Logout User");

        HomePage homePage = new HomePage();
        LoginPage loginPage = homePage.clickSignupLogin();
        homePage = loginPage.login(existingEmail, existingPassword);

        Assert.assertTrue(homePage.isLoggedInAsVisible(), "'Logged in as username' should be visible");

        loginPage = homePage.clickLogout();
        Assert.assertTrue(loginPage.isLoginHeaderVisible(), "Navigated to login page successfully after logout");

        LOGGER.info("Test Case 4 completed successfully!");
    }

    @Test(description = "Test Case 5: Register User with existing email", priority = 5)
    @Story("Duplicate User Registration Prevention")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Click 'Signup / Login'\n3. Verify 'New User Signup!'\n4. Enter name and already registered email\n5. Click 'Signup'\n6. Verify error 'Email Address already exist!'")
    public void testCase05_RegisterUserWithExistingEmail() {
        LOGGER.info("Starting Test Case 5: Register User with existing email: [{}]", existingEmail);

        HomePage homePage = new HomePage();
        LoginPage loginPage = homePage.clickSignupLogin();
        Assert.assertTrue(loginPage.isSignupHeaderVisible(), "'New User Signup!' should be visible");

        loginPage.enterSignupDetails("ExistingUser", existingEmail);
        loginPage.clickSignupButton();

        String errorMsg = loginPage.getSignupErrorMessage();
        LOGGER.info("Observed signup error: {}", errorMsg);
        Assert.assertTrue(errorMsg.toLowerCase().contains("already exist"),
                "Error message should indicate email already exists");

        LOGGER.info("Test Case 5 completed successfully!");
    }
}
