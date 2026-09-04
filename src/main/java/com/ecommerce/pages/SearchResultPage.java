package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing the Product Search Results page.
 */
public class SearchResultPage extends BasePage {

    private final By headingCounter = By.cssSelector("span.heading-counter");
    private final By firstProductContainer = By.cssSelector("ul.product_list li.ajax_block_product:first-of-type");
    private final By firstProductName = By.cssSelector("ul.product_list li.ajax_block_product:first-of-type a.product-name");
    private final By btnAddToCart = By.cssSelector("ul.product_list li.ajax_block_product:first-of-type a.ajax_add_to_cart_button");
    private final By modalLayerCart = By.id("layer_cart");
    private final By btnProceedToCheckout = By.cssSelector("a[title='Proceed to checkout']");

    @Step("Get search result count heading")
    public String getResultsCountText() {
        return getText(headingCounter, "Search Results Count", WaitStrategy.VISIBLE);
    }

    @Step("Get name of first product in search results")
    public String getFirstProductName() {
        return getText(firstProductName, "First Product Name", WaitStrategy.VISIBLE);
    }

    @Step("Hover over first product and click 'Add to cart'")
    public SearchResultPage addFirstProductToCart() {
        scrollToElement(firstProductContainer);
        hoverOver(firstProductContainer, "First Product Container");
        click(btnAddToCart, "Add To Cart Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Click 'Proceed to checkout' from Cart confirmation modal")
    public CartPage proceedToCheckoutFromModal() {
        click(btnProceedToCheckout, "Proceed to checkout modal button", WaitStrategy.CLICKABLE);
        return new CartPage();
    }

    @Step("Add product to cart and immediately proceed to cart checkout")
    public CartPage addProductAndProceedToCheckout() {
        addFirstProductToCart();
        return proceedToCheckoutFromModal();
    }

    @Step("Click on first product to view details")
    public ProductDetailsPage clickFirstProduct() {
        click(firstProductName, "First Product Link", WaitStrategy.CLICKABLE);
        return new ProductDetailsPage();
    }
}
