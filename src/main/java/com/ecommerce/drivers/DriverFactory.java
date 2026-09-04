package com.ecommerce.drivers;

import com.ecommerce.enums.BrowserType;
import com.ecommerce.enums.ConfigProperties;
import com.ecommerce.enums.RunMode;
import com.ecommerce.utils.ConfigReader;
import org.openqa.selenium.MutableCapabilities;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.remote.RemoteWebDriver;
import org.openqa.selenium.safari.SafariDriver;
import org.openqa.selenium.safari.SafariOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;

/**
 * Factory class to instantiate WebDriver for Local, Selenium Grid,
 * BrowserStack, or Sauce Labs.
 */
public final class DriverFactory {

    private static final Logger LOGGER = LoggerFactory.getLogger(DriverFactory.class);

    private DriverFactory() {
    }

    /**
     * Creates and returns a WebDriver instance configured for the specified browser
     * and run mode.
     */
    public static WebDriver createDriver(String browserName, String runModeStr) {
        BrowserType browserType = BrowserType.valueOf(browserName.trim().toUpperCase());
        RunMode runMode = RunMode.valueOf(runModeStr.trim().toUpperCase());
        boolean isHeadless = Boolean.parseBoolean(ConfigReader.get(ConfigProperties.HEADLESS));

        LOGGER.info("Initializing WebDriver for Browser: [{}] | RunMode: [{}] | Headless: [{}]", browserType, runMode,
                isHeadless);

        switch (runMode) {
            case GRID:
                return createRemoteGridDriver(browserType, isHeadless);
            case BROWSERSTACK:
                return createBrowserStackDriver(browserType);
            case SAUCELABS:
                return createSauceLabsDriver(browserType);
            case LOCAL:
            default:
                return createLocalDriver(browserType, isHeadless);
        }
    }

    /**
     * Local driver creation for Chrome, Firefox, Edge.
     */
    private static WebDriver createLocalDriver(BrowserType browserType, boolean isHeadless) {
        switch (browserType) {
            case FIREFOX:
                FirefoxOptions firefoxOptions = getFirefoxOptions(isHeadless);
                return new FirefoxDriver(firefoxOptions);
            case EDGE:
                EdgeOptions edgeOptions = getEdgeOptions(isHeadless);
                return new EdgeDriver(edgeOptions);
            case SAFARI:
                return new SafariDriver();
            case CHROME:
            default:
                ChromeOptions chromeOptions = getChromeOptions(isHeadless);
                return new ChromeDriver(chromeOptions);
        }
    }

    /**
     * Remote WebDriver targeting Selenium Grid Hub.
     */
    private static WebDriver createRemoteGridDriver(BrowserType browserType, boolean isHeadless) {
        String gridUrl = ConfigReader.get(ConfigProperties.GRID_URL);
        try {
            MutableCapabilities capabilities;
            switch (browserType) {
                case FIREFOX:
                    capabilities = getFirefoxOptions(isHeadless);
                    break;
                case EDGE:
                    capabilities = getEdgeOptions(isHeadless);
                    break;
                case SAFARI:
                    capabilities = new SafariOptions();
                    break;
                case CHROME:
                default:
                    capabilities = getChromeOptions(isHeadless);
                    break;
            }
            LOGGER.info("Connecting to Selenium Grid Hub at: {}", gridUrl);
            return new RemoteWebDriver(new URL(gridUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Selenium Grid URL: " + gridUrl, e);
        }
    }

    /**
     * Remote WebDriver targeting BrowserStack Automate.
     */
    private static WebDriver createBrowserStackDriver(BrowserType browserType) {
        String username = ConfigReader.get(ConfigProperties.BROWSERSTACK_USERNAME);
        String accessKey = ConfigReader.get(ConfigProperties.BROWSERSTACK_ACCESSKEY);
        String rawHubUrl = ConfigReader.get(ConfigProperties.BROWSERSTACK_URL);
        if (rawHubUrl == null || rawHubUrl.trim().isEmpty()) {
            rawHubUrl = "https://hub-cloud.browserstack.com/wd/hub";
        }
        String hubUrl = rawHubUrl;
        if (!hubUrl.contains("@") && username != null && !username.isEmpty()) {
            hubUrl = hubUrl.replace("https://", "https://" + username + ":" + accessKey + "@")
                    .replace("http://", "http://" + username + ":" + accessKey + "@");
        }

        MutableCapabilities capabilities;
        switch (browserType) {
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setBrowserVersion("latest");
                capabilities = firefoxOptions;
                break;
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setBrowserVersion("latest");
                capabilities = edgeOptions;
                break;
            case SAFARI:
                capabilities = new SafariOptions();
                break;
            case CHROME:
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setBrowserVersion("latest");

                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments(
                        "--host-resolver-rules=MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1, MAP *.doubleclick.net 127.0.0.1, MAP *.googleadservices.com 127.0.0.1");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--deny-permission-prompts"); // to deny permissions like location, camera
                chromeOptions.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" }); // Disable
                                                                                                              // "controlled
                // by automation"

                capabilities = chromeOptions;
                break;
        }

        HashMap<String, Object> bstackOptions = new HashMap<>();
        bstackOptions.put("os", "OS X");
        bstackOptions.put("osVersion", "Sonoma");
        bstackOptions.put("projectName", "E-Commerce Checkout Automation");
        bstackOptions.put("buildName", "Build-POM-1.0");
        bstackOptions.put("sessionName", "E2E Checkout Test - " + browserType);
        bstackOptions.put("userName", username);
        bstackOptions.put("accessKey", accessKey);
        capabilities.setCapability("bstack:options", bstackOptions);

        try {
            LOGGER.info("Connecting to BrowserStack Cloud at: {}", rawHubUrl);
            return new RemoteWebDriver(new URL(hubUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid BrowserStack Cloud URL: " + hubUrl, e);
        }
    }

    /**
     * Remote WebDriver targeting Sauce Labs.
     */
    private static WebDriver createSauceLabsDriver(BrowserType browserType) {
        String username = ConfigReader.get(ConfigProperties.SAUCELABS_USERNAME);
        String accessKey = ConfigReader.get(ConfigProperties.SAUCELABS_ACCESSKEY);
        String hubUrl = ConfigReader.get(ConfigProperties.SAUCELABS_URL);
        if (hubUrl == null || hubUrl.trim().isEmpty()) {
            hubUrl = "https://ondemand.us-west-1.saucelabs.com:443/wd/hub";
        }

        MutableCapabilities capabilities;
        switch (browserType) {
            case FIREFOX:
                FirefoxOptions firefoxOptions = new FirefoxOptions();
                firefoxOptions.setPlatformName("Windows 11");
                firefoxOptions.setBrowserVersion("latest");
                capabilities = firefoxOptions;
                break;
            case EDGE:
                EdgeOptions edgeOptions = new EdgeOptions();
                edgeOptions.setPlatformName("Windows 11");
                edgeOptions.setBrowserVersion("latest");
                capabilities = edgeOptions;
                break;
            case SAFARI:
                SafariOptions sauceSafari = new SafariOptions();
                sauceSafari.setPlatformName("macOS 13");
                sauceSafari.setBrowserVersion("latest");
                capabilities = sauceSafari;
                break;
            case CHROME:
            default:
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.setPlatformName("Windows 11");
                chromeOptions.setBrowserVersion("latest");
                chromeOptions.addArguments("--disable-notifications");
                chromeOptions.addArguments("--disable-popup-blocking");
                chromeOptions.addArguments("--disable-blink-features=AutomationControlled");
                chromeOptions.addArguments(
                        "--host-resolver-rules=MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1, MAP *.doubleclick.net 127.0.0.1, MAP *.googleadservices.com 127.0.0.1");
                chromeOptions.addArguments("--remote-allow-origins=*");
                chromeOptions.addArguments("--no-sandbox");
                chromeOptions.addArguments("--disable-dev-shm-usage");
                chromeOptions.addArguments("--deny-permission-prompts"); // to deny permissions like location, camera
                chromeOptions.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" }); // Disable
                                                                                                              // "controlled
                // by automation"

                capabilities = chromeOptions;
                break;
        }

        HashMap<String, Object> sauceOptions = new HashMap<>();
        sauceOptions.put("username", username);
        sauceOptions.put("accessKey", accessKey);
        sauceOptions.put("build", "E-Commerce-Checkout-Build-1.0");
        sauceOptions.put("name", "E2E Checkout Test - " + browserType);
        capabilities.setCapability("sauce:options", sauceOptions);

        try {
            LOGGER.info("Connecting to Sauce Labs Cloud at: {}", hubUrl);
            return new RemoteWebDriver(new URL(hubUrl), capabilities);
        } catch (MalformedURLException e) {
            throw new RuntimeException("Invalid Sauce Labs Cloud URL: " + hubUrl, e);
        }
    }

    public static ChromeOptions getChromeOptions(boolean isHeadless) {
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--disable-infobars");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--disable-blink-features=AutomationControlled");
        options.addArguments(
                "--host-resolver-rules=MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1, MAP *.doubleclick.net 127.0.0.1, MAP *.googleadservices.com 127.0.0.1");
        options.addArguments("--remote-allow-origins=*");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--deny-permission-prompts"); // to deny permissions like location, camera
        options.setExperimentalOption("excludeSwitches", new String[] { "enable-automation" }); // Disable "controlled
                                                                                                // by automation"
                                                                                                // message

        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        return options;
    }

    public static FirefoxOptions getFirefoxOptions(boolean isHeadless) {
        FirefoxOptions options = new FirefoxOptions();
        options.addPreference("dom.webnotifications.enabled", false);
        if (isHeadless) {
            options.addArguments("-headless");
            options.addArguments("--width=1920");
            options.addArguments("--height=1080");
        }
        return options;
    }

    public static EdgeOptions getEdgeOptions(boolean isHeadless) {
        EdgeOptions options = new EdgeOptions();
        options.addArguments("--disable-notifications");
        options.addArguments("--remote-allow-origins=*");
        if (isHeadless) {
            options.addArguments("--headless=new");
            options.addArguments("--window-size=1920,1080");
        } else {
            options.addArguments("--start-maximized");
        }
        return options;
    }
}
