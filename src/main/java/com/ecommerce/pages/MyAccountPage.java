package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing the User's "My Account" landing dashboard.
 */
public class MyAccountPage extends BasePage {

    private final By pageHeading = By.cssSelector("h1.page-heading");
    private final By linkOrderHistory = By.cssSelector("a[title='Orders']");
    private final By linkMyAddresses = By.cssSelector("a[title='Addresses']");
    private final By inputSearch = By.id("search_query_top");
    private final By btnSearch = By.name("submit_search");

    @Step("Get My Account page heading")
    public String getHeadingText() {
        return getText(pageHeading, "My Account Heading", WaitStrategy.VISIBLE);
    }

    @Step("Navigate to Order History and Details")
    public OrderHistoryPage clickOrderHistory() {
        click(linkOrderHistory, "Order History and Details Link", WaitStrategy.CLICKABLE);
        return new OrderHistoryPage();
    }

    @Step("Search for product: '{productName}' from My Account")
    public SearchResultPage searchProduct(String productName) {
        sendKeys(inputSearch, productName, "Search Input Box", WaitStrategy.VISIBLE);
        click(btnSearch, "Search Button", WaitStrategy.CLICKABLE);
        return new SearchResultPage();
    }
}
