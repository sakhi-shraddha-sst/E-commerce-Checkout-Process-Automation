package com.ecommerce.tests;

import com.ecommerce.drivers.Driver;
import com.ecommerce.drivers.DriverManager;
import com.ecommerce.enums.ConfigProperties;
import com.ecommerce.mock.MockEcommerceServer;
import com.ecommerce.utils.ConfigReader;
import org.openqa.selenium.WebDriver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.*;

import java.util.Map;

/**
 * BaseTest manages the setup and teardown of the WebDriver instance per test method
 * as well as the lifecycle of the MockEcommerceServer.
 */
@Listeners({com.ecommerce.listeners.AllureListener.class})
public class BaseTest implements org.testng.IHookable {

    protected static final Logger LOGGER = LoggerFactory.getLogger(BaseTest.class);

    @Override
    public void run(org.testng.IHookCallBack callBack, org.testng.ITestResult testResult) {
        String runModeActive = ConfigReader.get(ConfigProperties.RUNMODE).toUpperCase();
        String browserActive = ConfigReader.get(ConfigProperties.BROWSER).toUpperCase();

        try {
            io.qameta.allure.Allure.parameter("Execution Target", runModeActive);
            io.qameta.allure.Allure.parameter("Browser", browserActive);
            io.qameta.allure.Allure.label("tag", runModeActive);
            io.qameta.allure.Allure.label("tag", browserActive);
            io.qameta.allure.Allure.label("environment", runModeActive);

            io.qameta.allure.Allure.getLifecycle().updateTestCase(tc -> {
                tc.setHistoryId(runModeActive + "-" + browserActive + "-" + tc.getFullName());

                // Remove existing suite labels to prevent duplicate test listing in Allure
                tc.getLabels().removeIf(l -> "parentSuite".equals(l.getName()) || "suite".equals(l.getName()) || "subSuite".equals(l.getName()));
                tc.getLabels().add(new io.qameta.allure.model.Label().setName("parentSuite").setValue(runModeActive + " Execution Suite"));
                tc.getLabels().add(new io.qameta.allure.model.Label().setName("suite").setValue(runModeActive + " (" + browserActive + ")"));
                tc.getLabels().add(new io.qameta.allure.model.Label().setName("subSuite").setValue(testResult.getTestClass().getRealClass().getSimpleName()));

                String currentName = tc.getName();
                if (currentName != null && !currentName.contains("[" + runModeActive)) {
                    tc.setName(currentName + " [" + runModeActive + " - " + browserActive + "]");
                }
            });
        } catch (Exception e) {
            LOGGER.debug("Could not attach Allure environment parameters: {}", e.getMessage());
        }

        callBack.runTestMethod(testResult);
    }

    @BeforeSuite(alwaysRun = true)
    public void beforeSuite() {
        startMockServerIfNeeded();
    }

    private static synchronized void startMockServerIfNeeded() {
        String useMock = ConfigReader.get("use_mock_server");
        String url = ConfigReader.get(ConfigProperties.URL);
        if ("true".equalsIgnoreCase(useMock) || (url != null && url.contains("localhost"))) {
            LOGGER.info("Starting embedded MockEcommerceServer for deterministic test execution...");
            MockEcommerceServer.start();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void afterSuite() {
        MockEcommerceServer.stop();
    }

    @Parameters({"browser", "runMode"})
    @BeforeMethod
    public void setUp(@Optional("") String browser, @Optional("") String runMode, Object[] testData) {
        startMockServerIfNeeded();
        String browserToUse = browser;
        String runModeToUse = runMode;

        // If test is driven by DataProvider with browser column, use it
        if (testData != null && testData.length > 0 && testData[0] instanceof Map) {
            @SuppressWarnings("unchecked")
            Map<String, String> dataMap = (Map<String, String>) testData[0];
            if (dataMap.containsKey("browser") && !dataMap.get("browser").isEmpty()) {
                browserToUse = dataMap.get("browser");
            }
        }

        Driver.initDriver(browserToUse, runModeToUse);

        WebDriver driver = DriverManager.getDriver();
        attachCloudSessionLink(driver);
        String appUrl = ConfigReader.get(ConfigProperties.URL);
        LOGGER.info("Navigating to Application URL: {}", appUrl);
        driver.get(appUrl);
    }

    private void attachCloudSessionLink(WebDriver driver) {
        try {
            if (driver instanceof org.openqa.selenium.remote.RemoteWebDriver remoteDriver) {
                org.openqa.selenium.remote.SessionId sessionId = remoteDriver.getSessionId();
                if (sessionId != null) {
                    String runMode = ConfigReader.get(ConfigProperties.RUNMODE);
                    if ("saucelabs".equalsIgnoreCase(runMode)) {
                        String hubUrl = ConfigReader.get(ConfigProperties.SAUCELABS_URL);
                        String domain = (hubUrl != null && hubUrl.contains("eu-central-1"))
                                ? "app.eu-central-1.saucelabs.com"
                                : "app.saucelabs.com";
                        String sauceUrl = "https://" + domain + "/tests/" + sessionId;
                        io.qameta.allure.Allure.addAttachment("Sauce Labs Video & Logs", "text/html",
                                "<div style='font-family:sans-serif;font-size:14px;padding:10px;background:#f8f9fa;border-left:4px solid #d9534f;'>"
                                + "🎥 <b>Sauce Labs Cloud Session:</b><br/>"
                                + "<a href='" + sauceUrl + "' target='_blank' style='color:#0275d8;font-weight:bold;'>"
                                + "Open Video Replay & Logs (" + sessionId + ")</a></div>", ".html");
                        io.qameta.allure.Allure.addAttachment("Sauce Labs Session URL", "text/uri-list", sauceUrl);
                        LOGGER.info("Attached Sauce Labs Report Link to Allure: {}", sauceUrl);
                    } else if ("browserstack".equalsIgnoreCase(runMode)) {
                        String bstackUrl = "https://automate.browserstack.com/dashboard/v2/sessions/" + sessionId;
                        io.qameta.allure.Allure.addAttachment("BrowserStack Video & Logs", "text/html",
                                "<div style='font-family:sans-serif;font-size:14px;padding:10px;background:#f8f9fa;border-left:4px solid #007bff;'>"
                                + "🎥 <b>BrowserStack Cloud Session:</b><br/>"
                                + "<a href='" + bstackUrl + "' target='_blank' style='color:#0275d8;font-weight:bold;'>"
                                + "Open Session Dashboard (" + sessionId + ")</a></div>", ".html");
                        io.qameta.allure.Allure.addAttachment("BrowserStack Session URL", "text/uri-list", bstackUrl);
                        LOGGER.info("Attached BrowserStack Report Link to Allure: {}", bstackUrl);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.debug("Could not attach cloud session link to Allure: {}", e.getMessage());
        }
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        Driver.quitDriver();
    }
}
