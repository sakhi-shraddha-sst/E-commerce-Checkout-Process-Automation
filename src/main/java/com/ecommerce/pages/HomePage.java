package com.ecommerce.pages;

import com.ecommerce.actionbase.BasePage;
import com.ecommerce.enums.WaitStrategy;
import io.qameta.allure.Step;
import org.openqa.selenium.By;

/**
 * Page Object representing Automation Exercise Homepage (https://automationexercise.com).
 */
public class HomePage extends BasePage {

    private final By homeSlider = By.id("slider");
    private final By linkProducts = By.cssSelector("a[href='/products']");
    private final By linkCart = By.cssSelector("a[href='/view_cart']");
    private final By linkSignupLogin = By.cssSelector("a[href='/login']");
    private final By linkLogout = By.cssSelector("a[href='/logout']");
    private final By linkDeleteAccount = By.cssSelector("a[href='/delete_account']");
    private final By linkTestCases = By.cssSelector("a[href='/test_cases']");
    private final By linkContactUs = By.cssSelector("a[href='/contact_us']");
    private final By labelLoggedInUser = By.xpath("//li[contains(., 'Logged in as')]");

    // Subscription elements
    private final By headingSubscription = By.xpath("//div[@class='single-widget']//h2[contains(text(),'Subscription')]");
    private final By inputSubscribeEmail = By.id("susbscribe_email");
    private final By btnSubscribe = By.id("subscribe");
    private final By alertSubscribeSuccess = By.cssSelector("#success-subscribe .alert-success");

    // Categories
    private final By linkWomenCategory = By.xpath("//a[@data-toggle='collapse' and contains(@href,'Women')]");
    private final By linkWomenDress = By.xpath("//div[@id='Women']//a[contains(text(),'Dress')]");
    private final By linkMenCategory = By.xpath("//a[@data-toggle='collapse' and contains(@href,'Men')]");
    private final By linkMenTshirts = By.xpath("//div[@id='Men']//a[contains(text(),'Tshirts')]");

    // Recommended Items & Scroll Up
    private final By headingRecommendedItems = By.xpath("//h2[contains(text(), 'recommended items')]");
    private final By btnRecommendedAddToCart = By.xpath("(//div[@class='recommended_items']//a[contains(@class,'add-to-cart')])[1]");
    private final By linkViewCartModal = By.cssSelector("#cartModal a[href='/view_cart']");
    private final By btnScrollUp = By.id("scrollUp");
    private final By headingTopCarousel = By.xpath("(//div[@id='slider-carousel']//h2)[1]");

    @Step("Verify that home page is visible successfully")
    public boolean isHomePageVisible() {
        return isDisplayed(homeSlider, "Home Page Slider", WaitStrategy.VISIBLE);
    }

    @Step("Click on 'Signup / Login' button")
    public LoginPage clickSignupLogin() {
        dismissAdIfPresent();
        click(linkSignupLogin, "Signup / Login Button", WaitStrategy.CLICKABLE);
        return new LoginPage();
    }

    @Step("Click on 'Products' button")
    public ProductsPage clickProducts() {
        dismissAdIfPresent();
        click(linkProducts, "Products Button", WaitStrategy.CLICKABLE);
        if (getCurrentUrl().contains("#google_vignette")) {
            getDriver().navigate().to("https://automationexercise.com/products");
        }
        return new ProductsPage();
    }

    @Step("Click on 'Cart' button")
    public CartPage clickCart() {
        dismissAdIfPresent();
        click(linkCart, "Cart Button", WaitStrategy.CLICKABLE);
        if (getCurrentUrl().contains("#google_vignette")) {
            getDriver().navigate().to("https://automationexercise.com/view_cart");
        }
        return new CartPage();
    }

    @Step("Click on 'Test Cases' button")
    public TestCasesPage clickTestCases() {
        dismissAdIfPresent();
        click(linkTestCases, "Test Cases Button", WaitStrategy.CLICKABLE);
        return new TestCasesPage();
    }

    @Step("Click on 'Contact Us' button")
    public ContactUsPage clickContactUs() {
        dismissAdIfPresent();
        click(linkContactUs, "Contact Us Button", WaitStrategy.CLICKABLE);
        return new ContactUsPage();
    }

    @Step("Click 'Delete Account' button")
    public SignupPage clickDeleteAccount() {
        dismissAdIfPresent();
        click(linkDeleteAccount, "Delete Account Button", WaitStrategy.CLICKABLE);
        return new SignupPage();
    }

    @Step("Verify 'Logged in as username' is visible")
    public boolean isLoggedInAsVisible() {
        return isDisplayed(labelLoggedInUser, "Logged in as User label", WaitStrategy.VISIBLE);
    }

    @Step("Get logged in user status text")
    public String getLoggedInUserText() {
        return getText(labelLoggedInUser, "Logged in as User", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Logout' button")
    public LoginPage clickLogout() {
        dismissAdIfPresent();
        click(linkLogout, "Logout Button", WaitStrategy.CLICKABLE);
        return new LoginPage();
    }

    @Step("Scroll down to footer")
    public HomePage scrollToFooter() {
        scrollToElement(headingSubscription);
        return this;
    }

    @Step("Verify 'SUBSCRIPTION' is visible")
    public boolean isSubscriptionVisible() {
        return isDisplayed(headingSubscription, "Subscription Heading", WaitStrategy.VISIBLE);
    }

    @Step("Subscribe to newsletter with email: '{email}'")
    public HomePage subscribe(String email) {
        sendKeys(inputSubscribeEmail, email, "Subscription Email Field", WaitStrategy.VISIBLE);
        click(btnSubscribe, "Subscribe Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Verify subscription success message is visible")
    public boolean isSubscriptionSuccessVisible() {
        return isDisplayed(alertSubscribeSuccess, "Subscription Success Alert", WaitStrategy.VISIBLE);
    }

    @Step("Scroll to 'RECOMMENDED ITEMS'")
    public HomePage scrollToRecommendedItems() {
        scrollToElement(headingRecommendedItems);
        return this;
    }

    @Step("Verify 'RECOMMENDED ITEMS' are visible")
    public boolean isRecommendedItemsVisible() {
        return isDisplayed(headingRecommendedItems, "Recommended Items Heading", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Add To Cart' on recommended item and proceed to cart")
    public CartPage addRecommendedItemToCart() {
        dismissAdIfPresent();
        scrollToElement(btnRecommendedAddToCart);
        jsClick(btnRecommendedAddToCart, "Recommended Add to Cart Button");
        click(linkViewCartModal, "View Cart Modal Link", WaitStrategy.CLICKABLE);
        return new CartPage();
    }

    @Step("Click scroll-up arrow button")
    public HomePage clickScrollUpArrow() {
        click(btnScrollUp, "Scroll Up Button", WaitStrategy.CLICKABLE);
        return this;
    }

    @Step("Scroll up to top of page using JavaScript")
    public HomePage scrollUpToTop() {
        jsExecutor().executeScript("window.scrollTo(0, 0);");
        return this;
    }

    @Step("Verify top text is visible after scrolling up")
    public boolean isTopTextVisible() {
        return isDisplayed(headingTopCarousel, "Top Carousel Heading", WaitStrategy.VISIBLE);
    }

    @Step("Click 'Women' -> 'Dress' category")
    public ProductsPage clickWomenDressCategory() {
        scrollToElement(linkWomenCategory);
        click(linkWomenCategory, "Women Category Link", WaitStrategy.CLICKABLE);
        click(linkWomenDress, "Women Dress Subcategory Link", WaitStrategy.CLICKABLE);
        return new ProductsPage();
    }

    @Step("Click 'Men' -> 'Tshirts' category")
    public ProductsPage clickMenTshirtsCategory() {
        scrollToElement(linkMenCategory);
        click(linkMenCategory, "Men Category Link", WaitStrategy.CLICKABLE);
        click(linkMenTshirts, "Men Tshirts Subcategory Link", WaitStrategy.CLICKABLE);
        return new ProductsPage();
    }
}
