package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise All Products / Search Results Page (/products).
 */
public class ProductsPage extends BasePage {

    private final By titleAllProducts = By.xpath("//h2[@class='title text-center']");
    private final By inputSearch = By.id("search_product");
    private final By btnSearch = By.id("submit_search");
    private final By firstProductCard = By.xpath("(//div[@class='features_items']//div[contains(@class,'product-image-wrapper')])[1]");
    private final By firstProductName = By.xpath("(//div[@class='features_items']//div[contains(@class,'productinfo')]/p)[1]");
    private final By firstProductPrice = By.xpath("(//div[@class='features_items']//div[contains(@class,'productinfo')]/h2)[1]");
    private final By btnAddToCart = By.xpath("(//div[@class='features_items']//div[contains(@class,'productinfo')]//a[contains(@class,'add-to-cart')])[1]");
    private final By btnSecondAddToCart = By.xpath("(//div[@class='features_items']//div[contains(@class,'productinfo')]//a[contains(@class,'add-to-cart')])[2]");
    private final By linkViewProductFirst = By.xpath("(//div[@class='features_items']//a[contains(@href,'/product_details/')])[1]");
    private final By linkViewCartFromModal = By.cssSelector("#cartModal a[href='/view_cart']");
    private final By btnCloseModal = By.cssSelector("#cartModal button.close-modal");

    // Brands sidebar
    private final By linkBrandPolo = By.xpath("//div[@class='brands-name']//a[contains(@href,'Polo')]");
    private final By linkBrandMadame = By.xpath("//div[@class='brands-name']//a[contains(@href,'Madame')]");
    private final By titleBrandHeading = By.xpath("//h2[@class='title text-center' and contains(text(),'Brand')]");

    @Step("Verify that user is navigated to ALL PRODUCTS page successfully")
    public boolean isProductsPageVisible() {
        return isDisplayed(titleAllProducts, "All Products Title", WaitStrategy.VISIBLE);
    }

    @Step("Get product page title text")
    public String getPageHeading() {
        return getText(titleAllProducts, "Product Page Heading", WaitStrategy.VISIBLE);
    }

    @Step("Search for product: '{productName}'")
    public ProductsPage searchProduct(String productName) {
        dismissAdIfPresent();
        sendKeys(inputSearch, productName, "Search Product Input", WaitStrategy.VISIBLE);
        click(btnSearch, "Submit Search Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Get name of first product in list")
    public String getFirstProductName() {
        return getText(firstProductName, "First Product Name", WaitStrategy.VISIBLE);
    }

    @Step("Get price of first product in list")
    public String getFirstProductPrice() {
        return getText(firstProductPrice, "First Product Price", WaitStrategy.VISIBLE);
    }

    @Step("Add first product to cart")
    public ProductsPage addFirstProductToCart() {
        dismissAdIfPresent();
        scrollToElement(firstProductCard);
        click(btnAddToCart, "Add to Cart Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Add second product to cart")
    public ProductsPage addSecondProductToCart() {
        dismissAdIfPresent();
        click(btnSecondAddToCart, "Second Add to Cart Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Click 'View Product' on first product")
    public ProductDetailPage clickViewProductFirst() {
        dismissAdIfPresent();
        scrollToElement(linkViewProductFirst);
        click(linkViewProductFirst, "First View Product Link", WaitStrategy.CLICKABLE);
        return new ProductDetailPage();
    }

    @Step("Click 'View Cart' link on added confirmation modal")
    public CartPage clickProceedToCartFromModal() {
        click(linkViewCartFromModal, "View Cart Modal Link", WaitStrategy.CLICKABLE);
        return new CartPage();
    }

    @Step("Click 'Continue Shopping' on added modal")
    public ProductsPage clickContinueShopping() {
        click(btnCloseModal, "Continue Shopping Modal Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Add product to cart and immediately proceed to cart")
    public CartPage addProductAndProceedToCart() {
        addFirstProductToCart();
        return clickProceedToCartFromModal();
    }

    @Step("Click 'Polo' brand link in sidebar")
    public ProductsPage clickBrandPolo() {
        scrollToElement(linkBrandPolo);
        click(linkBrandPolo, "Polo Brand Link", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Click 'Madame' brand link in sidebar")
    public ProductsPage clickBrandMadame() {
        scrollToElement(linkBrandMadame);
        click(linkBrandMadame, "Madame Brand Link", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify Brand page title is visible")
    public boolean isBrandTitleVisible() {
        return isDisplayed(titleBrandHeading, "Brand Page Heading", WaitStrategy.VISIBLE);
    }
}
