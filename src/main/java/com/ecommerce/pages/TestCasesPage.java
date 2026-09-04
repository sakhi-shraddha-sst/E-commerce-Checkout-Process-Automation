package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object representing Automation Exercise Test Cases Page (/test_cases).
 */
public class TestCasesPage extends BasePage {

    private final By headingTestCases = By.xpath("//h2[@class='title text-center']//b[contains(text(), 'Test Cases')]");
    private final By testCasePanels = By.cssSelector(".panel-group .panel");

    @Step("Verify that user is navigated to test cases page successfully")
    public boolean isTestCasesPageVisible() {
        return isDisplayed(headingTestCases, "Test Cases Heading", WaitStrategy.VISIBLE);
    }

    @Step("Get count of listed test cases")
    public int getTestCasesCount() {
        List<WebElement> panels = findElements(testCasePanels);
        return panels.size();
    }
}
