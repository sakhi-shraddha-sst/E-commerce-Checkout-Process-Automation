package com.ecommerce.tests;

import com.ecommerce.models.ContactUsModel;
import com.ecommerce.pages.ContactUsPage;
import com.ecommerce.pages.HomePage;
import com.ecommerce.utils.TestDataFactory;
import io.qameta.allure.*;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;

/**
 * Contact Us Test Suite covering Official Automation Exercise Scenarios:
 * - Test Case 6: Contact Us Form
 * Validates customer support inquiry form and file attachments.
 * 
 */
@Epic("Customer Support & Inquiries")
@Feature("Contact Us Workflows")
public class ContactUsTest extends BaseTest {

    @Test(description = "Test Case 6: Contact Us Form", priority = 1)
    @Story("Submit Contact Inquiry")
    @Severity(SeverityLevel.NORMAL)
    @Description("1. Navigate to url\n2. Verify home page\n3. Click 'Contact Us'\n4. Verify 'GET IN TOUCH'\n5. Enter name, email, subject and message\n6. Upload file\n7. Click 'Submit' and accept alert\n8. Verify success message 'Success! Your details have been submitted successfully.'\n9. Click 'Home' and verify landing")
    public void testCase06_ContactUsForm() {
        LOGGER.info("Starting Test Case 6: Contact Us Form");
        ContactUsModel inquiry = TestDataFactory.generateContactInquiry();

        HomePage homePage = new HomePage();
        Assert.assertTrue(homePage.isHomePageVisible(), "Home page should be visible");

        ContactUsPage contactUsPage = homePage.clickContactUs();
        Assert.assertTrue(contactUsPage.isGetInTouchVisible(), "'GET IN TOUCH' should be visible");

        contactUsPage.fillContactForm(
                inquiry.getName(),
                inquiry.getEmail(),
                inquiry.getSubject(),
                inquiry.getMessage());

        String sampleFilePath = new File("src/test/resources/testdata/sample.txt").getAbsolutePath();
        contactUsPage.uploadFile(sampleFilePath);

        contactUsPage.submitFormAndAcceptAlert();

        Assert.assertTrue(contactUsPage.isSuccessMessageVisible(), "Success alert should be visible");
        String successMsg = contactUsPage.getSuccessAlertText();
        LOGGER.info("Contact form response: {}", successMsg);
        Assert.assertTrue(successMsg.contains("Success! Your details have been submitted successfully."),
                "Success message should confirm submission");

        homePage = contactUsPage.clickHome();
        Assert.assertTrue(homePage.isHomePageVisible(), "Returned to home page successfully");

        LOGGER.info("Test Case 6 completed successfully!");
    }
}
