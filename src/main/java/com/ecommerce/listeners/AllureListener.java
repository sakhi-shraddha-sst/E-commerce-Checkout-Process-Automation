package com.ecommerce.listeners;

import com.ecommerce.drivers.DriverManager;
import com.ecommerce.enums.ConfigProperties;
import com.ecommerce.utils.ConfigReader;
import com.ecommerce.utils.ScreenshotUtils;
import io.qameta.allure.Allure;
import io.qameta.allure.Attachment;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.remote.SessionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.ITestContext;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * TestNG Listener integrating Allure test lifecycle events, automatic screenshot capture,
 * and Sauce Labs / Cloud session links.
 */
public class AllureListener implements ITestListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(AllureListener.class);

    @Override
    public void onStart(ITestContext context) {
        LOGGER.info("=== Starting Test Suite: [{}] ===", context.getName());
        writeEnvironmentProperties();
    }

    @Override
    public void onFinish(ITestContext context) {
        LOGGER.info("=== Completed Test Suite: [{}] ===", context.getName());
    }

    @Override
    public void onTestStart(ITestResult result) {
        LOGGER.info("--> Test Started: [{}]", result.getMethod().getMethodName());
    }

    private void writeEnvironmentProperties() {
        try {
            java.io.File resultsDir = new java.io.File("target/allure-results");
            if (!resultsDir.exists()) {
                resultsDir.mkdirs();
            }
            java.io.File envFile = new java.io.File(resultsDir, "environment.properties");
            java.util.Properties props = new java.util.Properties();
            props.setProperty("Execution.RunMode", ConfigReader.get(ConfigProperties.RUNMODE).toUpperCase());
            props.setProperty("Browser", ConfigReader.get(ConfigProperties.BROWSER).toUpperCase());
            props.setProperty("Application.URL", ConfigReader.get(ConfigProperties.URL));
            props.setProperty("Operating.System", System.getProperty("os.name"));
            props.setProperty("Java.Version", System.getProperty("java.version"));
            try (java.io.FileOutputStream fos = new java.io.FileOutputStream(envFile, false)) {
                props.store(fos, "Allure Environment Overview");
            }

            java.io.File catSrc = new java.io.File("src/test/resources/categories.json");
            if (catSrc.exists()) {
                java.nio.file.Files.copy(catSrc.toPath(), new java.io.File(resultsDir, "categories.json").toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (Exception e) {
            LOGGER.debug("Could not write Allure environment or categories: {}", e.getMessage());
        }
    }

    @Override
    public void onTestSuccess(ITestResult result) {
        LOGGER.info("✔ Test Passed: [{}]", result.getMethod().getMethodName());
        attachCloudSessionLink();
    }

    @Override
    public void onTestFailure(ITestResult result) {
        LOGGER.error("✘ Test Failed: [{}] - Error: {}", result.getMethod().getMethodName(), result.getThrowable().getMessage());
        attachCloudSessionLink();
        saveScreenshotOnFailure();
        saveTextLog(getStackTrace(result.getThrowable()));
    }

    @Override
    public void onTestSkipped(ITestResult result) {
        LOGGER.warn("↷ Test Skipped: [{}]", result.getMethod().getMethodName());
    }

    /**
     * Attaches Sauce Labs / Cloud execution video & dashboard link into the Allure Report.
     */
    private void attachCloudSessionLink() {
        try {
            WebDriver driver = DriverManager.getDriver();
            if (driver instanceof RemoteWebDriver remoteDriver) {
                SessionId sessionId = remoteDriver.getSessionId();
                if (sessionId != null) {
                    String runMode = ConfigReader.get(ConfigProperties.RUNMODE);
                    if ("saucelabs".equalsIgnoreCase(runMode)) {
                        String hubUrl = ConfigReader.get(ConfigProperties.SAUCELABS_URL);
                        String domain = (hubUrl != null && hubUrl.contains("eu-central-1"))
                                ? "app.eu-central-1.saucelabs.com"
                                : "app.saucelabs.com";
                        String sauceUrl = "https://" + domain + "/tests/" + sessionId;
                        Allure.addAttachment("Sauce Labs Cloud Session", "text/html",
                                "<div style='font-family:sans-serif;font-size:14px;padding:10px;background:#f8f9fa;border-left:4px solid #d9534f;'>"
                                + "🎥 <b>Sauce Labs Video & Logs:</b><br/>"
                                + "<a href='" + sauceUrl + "' target='_blank' style='color:#0275d8;font-weight:bold;'>"
                                + "Open Session (" + sessionId + ")</a></div>", ".html");
                        LOGGER.info("Attached Sauce Labs Report Link to Allure: {}", sauceUrl);
                    } else if ("browserstack".equalsIgnoreCase(runMode)) {
                        Allure.addAttachment("BrowserStack Session ID", "text/plain", sessionId.toString());
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Could not attach cloud session link: {}", e.getMessage());
        }
    }

    /**
     * Attaches PNG screenshot to the Allure report on test failure.
     */
    @Attachment(value = "Failure Screenshot", type = "image/png")
    public byte[] saveScreenshotOnFailure() {
        return ScreenshotUtils.getScreenshotBytes();
    }

    /**
     * Attaches plain text log to the Allure report.
     */
    @Attachment(value = "Failure Stack Trace", type = "text/plain")
    public String saveTextLog(String message) {
        return message;
    }

    private String getStackTrace(Throwable throwable) {
        if (throwable == null) return "";
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        throwable.printStackTrace(pw);
        return sw.toString();
    }
}
