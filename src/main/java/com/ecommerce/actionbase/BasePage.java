package com.ecommerce.actionbase;

import com.ecommerce.constants.FrameworkConstants;
import com.ecommerce.drivers.DriverManager;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;

/**
 * BasePage encapsulates core WebDriver operations with robust explicit waits,
 * automatic stale element handling, JS execution, and Allure reporting steps.
 */
public class BasePage {

    private static final Logger LOGGER = LoggerFactory.getLogger(BasePage.class);

    protected BasePage() {}

    protected WebDriver getDriver() {
        return DriverManager.getDriver();
    }

    protected JavascriptExecutor jsExecutor() {
        return (JavascriptExecutor) getDriver();
    }

    /**
     * Explicitly waits for an element based on the provided WaitStrategy.
     */
    protected WebElement performExplicitWait(By by, WaitStrategy waitStrategy) {
        WebDriverWait wait = new WebDriverWait(getDriver(), Duration.ofSeconds(FrameworkConstants.getExplicitWait()));
        switch (waitStrategy) {
            case CLICKABLE:
                return wait.until(ExpectedConditions.elementToBeClickable(by));
            case PRESENCE:
                return wait.until(ExpectedConditions.presenceOfElementLocated(by));
            case VISIBLE:
                return wait.until(ExpectedConditions.visibilityOfElementLocated(by));
            case NONE:
            default:
                return getDriver().findElement(by);
        }
    }

    /**
     * Clicks an element with auto-wait and stale element retry logic.
     */
    @Step("Click on '{elementName}'")
    protected void click(By by, String elementName, WaitStrategy waitStrategy) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = performExplicitWait(by, waitStrategy);
                highlightElement(element);
                element.click();
                LOGGER.info("Clicked on [{}]", elementName);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                LOGGER.warn("Stale element for [{}]. Retrying attempt {} of 3", elementName, attempts);
            } catch (ElementClickInterceptedException e) {
                LOGGER.warn("Click intercepted on [{}]. Dismissing potential ad overlays and falling back to JavaScript click.", elementName);
                dismissAdIfPresent();
                jsClick(by, elementName);
                return;
            }
        }
    }

    /**
     * Safely hides Google Ad iframes if they overlap the viewport.
     */
    public void dismissAdIfPresent() {
        try {
            jsExecutor().executeScript(
                "document.querySelectorAll('iframe[id*=\"aswift\"], iframe[id*=\"ad_iframe\"], iframe[id*=\"google_ads\"], div[id*=\"aswift\"]').forEach(el => el.remove());"
            );
        } catch (Exception ignored) {
        }
    }

    /**
     * Types a value into a field with auto-clearing and wait.
     */
    @Step("Type '{value}' into '{elementName}'")
    protected void sendKeys(By by, String value, String elementName, WaitStrategy waitStrategy) {
        int attempts = 0;
        while (attempts < 3) {
            try {
                WebElement element = performExplicitWait(by, waitStrategy);
                highlightElement(element);
                element.clear();
                element.sendKeys(value);
                LOGGER.info("Entered [{}] into [{}]", value, elementName);
                return;
            } catch (StaleElementReferenceException e) {
                attempts++;
                LOGGER.warn("Stale element for [{}]. Retrying sendKeys attempt {} of 3", elementName, attempts);
            }
        }
    }

    /**
     * Retrieves visible text from an element.
     */
    @Step("Get text from '{elementName}'")
    protected String getText(By by, String elementName, WaitStrategy waitStrategy) {
        WebElement element = performExplicitWait(by, waitStrategy);
        String text = element.getText().trim();
        LOGGER.info("Retrieved text from [{}]: '{}'", elementName, text);
        return text;
    }

    /**
     * Checks if element is displayed on the page.
     */
    @Step("Check if '{elementName}' is displayed")
    protected boolean isDisplayed(By by, String elementName, WaitStrategy waitStrategy) {
        try {
            WebElement element = performExplicitWait(by, waitStrategy);
            boolean displayed = element.isDisplayed();
            LOGGER.info("[{}] is displayed: {}", elementName, displayed);
            return displayed;
        } catch (Exception e) {
            LOGGER.warn("[{}] is not displayed: {}", elementName, e.getMessage());
            return false;
        }
    }

    /**
     * Selects from standard HTML dropdown by visible text.
     */
    @Step("Select '{visibleText}' from dropdown '{elementName}'")
    protected void selectByVisibleText(By by, String visibleText, String elementName, WaitStrategy waitStrategy) {
        dismissAdIfPresent();
        WebElement element = performExplicitWait(by, waitStrategy);
        try {
            Select select = new Select(element);
            select.selectByVisibleText(visibleText);
        } catch (Exception e) {
            dismissAdIfPresent();
            jsExecutor().executeScript(
                "var select = arguments[0];" +
                "for (var i = 0; i < select.options.length; i++) {" +
                "  if (select.options[i].text.trim() === arguments[1].trim()) {" +
                "    select.selectedIndex = i;" +
                "    select.dispatchEvent(new Event('change'));" +
                "    break;" +
                "  }" +
                "}", element, visibleText);
        }
        LOGGER.info("Selected [{}] from [{}]", visibleText, elementName);
    }

    /**
     * Selects from standard HTML dropdown by value attribute.
     */
    @Step("Select value '{value}' from dropdown '{elementName}'")
    protected void selectByValue(By by, String value, String elementName, WaitStrategy waitStrategy) {
        dismissAdIfPresent();
        WebElement element = performExplicitWait(by, waitStrategy);
        try {
            Select select = new Select(element);
            select.selectByValue(value);
        } catch (Exception e) {
            dismissAdIfPresent();
            jsExecutor().executeScript(
                "arguments[0].value = arguments[1];" +
                "arguments[0].dispatchEvent(new Event('change'));", element, value);
        }
        LOGGER.info("Selected value [{}] from [{}]", value, elementName);
    }

    /**
     * Scrolls element into view using JavaScript.
     */
    protected void scrollToElement(By by) {
        WebElement element = performExplicitWait(by, WaitStrategy.PRESENCE);
        jsExecutor().executeScript("arguments[0].scrollIntoView({behavior: 'smooth', block: 'center'});", element);
    }

    /**
     * Clicks an element via JavaScript execution.
     */
    @Step("JS Click on '{elementName}'")
    protected void jsClick(By by, String elementName) {
        WebElement element = performExplicitWait(by, WaitStrategy.PRESENCE);
        jsExecutor().executeScript("arguments[0].click();", element);
        LOGGER.info("JavaScript clicked on [{}]", elementName);
    }

    /**
     * Hovers over an element.
     */
    @Step("Hover over '{elementName}'")
    protected void hoverOver(By by, String elementName) {
        WebElement element = performExplicitWait(by, WaitStrategy.VISIBLE);
        Actions actions = new Actions(getDriver());
        actions.moveToElement(element).perform();
        LOGGER.info("Hovered over [{}]", elementName);
    }

    /**
     * Finds list of elements.
     */
    protected List<WebElement> findElements(By by) {
        return getDriver().findElements(by);
    }

    /**
     * Highlights an element temporarily for visual debugging.
     */
    protected void highlightElement(WebElement element) {
        try {
            jsExecutor().executeScript("arguments[0].setAttribute('style', 'border: 2px dashed #00bcd4;');", element);
        } catch (Exception ignored) {
            // Highlighting is non-critical
        }
    }

    public String getPageTitle() {
        return getDriver().getTitle();
    }

    public String getCurrentUrl() {
        return getDriver().getCurrentUrl();
    }
}
