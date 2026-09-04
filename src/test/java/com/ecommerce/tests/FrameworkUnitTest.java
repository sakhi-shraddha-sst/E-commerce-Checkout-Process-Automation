package com.ecommerce.tests;

import com.ecommerce.constants.FrameworkConstants;
import com.ecommerce.enums.ConfigProperties;
import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.ExcelUtils;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.File;
import java.util.List;
import java.util.Map;

/**
 * Validates framework foundational components: Configuration Reader and Excel Data-Driven Reader.
 */
public class FrameworkUnitTest {

    @Test(description = "Verify config.properties loads expected default keys")
    public void testConfigReader() {
        String url = ConfigReader.get(ConfigProperties.URL);
        Assert.assertNotNull(url, "URL should be configured");
        Assert.assertTrue(url.contains("http"), "URL should be a valid web protocol address");

        String browser = ConfigReader.get(ConfigProperties.BROWSER);
        Assert.assertNotNull(browser, "Browser should be configured");

        String runmode = ConfigReader.get(ConfigProperties.RUNMODE);
        Assert.assertNotNull(runmode, "RunMode should be configured");
    }

    @Test(description = "Verify Excel file exists and reads data rows properly")
    public void testExcelUtils() {
        File excelFile = new File(FrameworkConstants.getTestDataExcelPath());
        Assert.assertTrue(excelFile.exists(), "testdata.xlsx must exist at: " + excelFile.getAbsolutePath());

        List<Map<String, String>> checkoutData = ExcelUtils.getTestData(FrameworkConstants.getCheckoutDataSheet());
        Assert.assertFalse(checkoutData.isEmpty(), "CheckoutData sheet must have rows");
        Assert.assertEquals(checkoutData.get(0).get("email"), "autotest_user_pom@example.com");
        Assert.assertEquals(checkoutData.get(0).get("product"), "Dress");

        List<Map<String, String>> userData = ExcelUtils.getTestData(FrameworkConstants.getUserDataSheet());
        Assert.assertTrue(userData.size() >= 2, "UserData sheet must have multiple rows for data-driven testing");
    }

    @Test(description = "Verify AES-256 / Base64 password encryption and decryption")
    public void testEncryptionUtils() {
        String originalSecret = "SuperSecretPassword@123!";
        String encrypted = com.ecommerce.utils.EncryptionUtils.encrypt(originalSecret);

        Assert.assertNotNull(encrypted);
        Assert.assertTrue(encrypted.startsWith("ENC("), "Encrypted token must start with ENC(");
        Assert.assertTrue(encrypted.endsWith(")"), "Encrypted token must end with )");

        String decrypted = com.ecommerce.utils.EncryptionUtils.decrypt(encrypted);
        Assert.assertEquals(decrypted, originalSecret, "Decrypted text must match original secret");
    }

    @Test(description = "Verify Datafaker TestDataFactory generates non-null collision-free data")
    public void testTestDataFactory() {
        com.ecommerce.models.UserModel user1 = com.ecommerce.utils.TestDataFactory.generateRandomUser();
        com.ecommerce.models.UserModel user2 = com.ecommerce.utils.TestDataFactory.generateRandomUser();

        Assert.assertNotNull(user1.getEmail());
        Assert.assertNotNull(user2.getEmail());
        Assert.assertNotEquals(user1.getEmail(), user2.getEmail(), "Subsequent generated emails must be unique");
        Assert.assertNotNull(user1.getFirstName());
        Assert.assertNotNull(user1.getAddress());
        Assert.assertNotNull(user1.getMobile());

        com.ecommerce.models.CheckoutModel payment = com.ecommerce.utils.TestDataFactory.generatePaymentData();
        Assert.assertNotNull(payment.getCardNumber());
        Assert.assertEquals(payment.getCardNumber().length(), 16);
        Assert.assertNotNull(payment.getCvc());
    }

    @Test(description = "Verify JsonUtils deserializes scenario test data files")
    public void testJsonUtils() {
        com.ecommerce.models.CheckoutModel checkout = com.ecommerce.utils.JsonUtils.deserialize(
                "src/test/resources/testdata/checkout.json",
                com.ecommerce.models.CheckoutModel.class
        );
        // Note: checkout.json contains "visaPayment" nested or direct; let's test that file is readable
        Assert.assertNotNull(checkout);
    }
}
