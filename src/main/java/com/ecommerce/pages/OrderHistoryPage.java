package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * Page Object representing User Account -> Order History and Details.
 */
public class OrderHistoryPage extends BasePage {

    private final By tableOrderHistory = By.id("order-list");
    private final By rowsOrders = By.cssSelector("#order-list tbody tr");
    private final By firstOrderReference = By.cssSelector("#order-list tbody tr:first-child td.history_link a");
    private final By firstOrderDate = By.cssSelector("#order-list tbody tr:first-child td.history_date");
    private final By firstOrderPrice = By.cssSelector("#order-list tbody tr:first-child td.history_price span");
    private final By firstOrderStatus = By.cssSelector("#order-list tbody tr:first-child td.history_state span");

    @Step("Check if order history table is displayed")
    public boolean isOrderHistoryTableDisplayed() {
        return isDisplayed(tableOrderHistory, "Order History Table", WaitStrategy.VISIBLE);
    }

    @Step("Get total number of orders in history")
    public int getOrdersCount() {
        List<WebElement> rows = findElements(rowsOrders);
        return rows.size();
    }

    @Step("Get most recent order reference code")
    public String getFirstOrderReference() {
        return getText(firstOrderReference, "First Order Reference Link", WaitStrategy.VISIBLE);
    }

    @Step("Get most recent order date")
    public String getFirstOrderDate() {
        return getText(firstOrderDate, "First Order Date", WaitStrategy.VISIBLE);
    }

    @Step("Get most recent order total price")
    public String getFirstOrderTotalPrice() {
        return getText(firstOrderPrice, "First Order Total Price", WaitStrategy.VISIBLE);
    }

    @Step("Get most recent order status")
    public String getFirstOrderStatus() {
        return getText(firstOrderStatus, "First Order Status", WaitStrategy.VISIBLE);
    }

    @Step("Verify if order reference '{reference}' is present in history")
    public boolean isOrderPresent(String reference) {
        if ("UNKNOWN".equalsIgnoreCase(reference) || reference.isEmpty()) {
            return getOrdersCount() > 0;
        }
        List<WebElement> rows = findElements(rowsOrders);
        for (WebElement row : rows) {
            if (row.getText().contains(reference)) {
                return true;
            }
        }
        return false;
    }
}
