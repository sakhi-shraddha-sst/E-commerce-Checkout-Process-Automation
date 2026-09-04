# E-Commerce Test Automation Framework (All 26 Scenarios)

[![Java](https://img.shields.io/badge/Java-17-orange.svg)](https://www.oracle.com/java/)
[![Selenium](https://img.shields.io/badge/Selenium-4.23.0-green.svg)](https://www.selenium.dev/)
[![TestNG](https://img.shields.io/badge/TestNG-7.9.0-blue.svg)](https://testng.org/)
[![Apache POI](https://img.shields.io/badge/Apache_POI-5.3.0-red.svg)](https://poi.apache.org/)
[![Allure Report](https://img.shields.io/badge/Allure-2.24.0-yellow.svg)](https://qameta.io/allure-report/)

An enterprise-grade Selenium WebDriver Page Object Model (POM) test automation framework in Java automating **all 26 official test cases** from [Automation Exercise Test Cases](https://www.automationexercise.com/test_cases). Features **Data-Driven Testing (Apache POI)**, **Allure Reporting**, and multi-browser **Parallel Execution** across Local, **Selenium Grid 4**, and Cloud providers (**Sauce Labs** and **BrowserStack**).

---

## 🎯 Complete Matrix of All 26 Automated Test Cases

| # | Suite Class | Test Case Title | Status |
| :-: | :--- | :--- | :-: |
| **1** | `AuthenticationTest` | **Test Case 1**: Register User | **PASSED** ✔ |
| **2** | `AuthenticationTest` | **Test Case 2**: Login User with correct email and password | **PASSED** ✔ |
| **3** | `AuthenticationTest` | **Test Case 3**: Login User with incorrect email and password | **PASSED** ✔ |
| **4** | `AuthenticationTest` | **Test Case 4**: Logout User | **PASSED** ✔ |
| **5** | `AuthenticationTest` | **Test Case 5**: Register User with existing email | **PASSED** ✔ |
| **6** | `ContactUsTest` | **Test Case 6**: Contact Us Form (with file upload) | **PASSED** ✔ |
| **7** | `NavigationAndMiscTest` | **Test Case 7**: Verify Test Cases Page | **PASSED** ✔ |
| **8** | `ProductCatalogTest` | **Test Case 8**: Verify All Products and product detail page | **PASSED** ✔ |
| **9** | `ProductCatalogTest` | **Test Case 9**: Search Product | **PASSED** ✔ |
| **10** | `NavigationAndMiscTest` | **Test Case 10**: Verify Subscription in home page | **PASSED** ✔ |
| **11** | `NavigationAndMiscTest` | **Test Case 11**: Verify Subscription in Cart page | **PASSED** ✔ |
| **12** | `CartTest` | **Test Case 12**: Add Products in Cart | **PASSED** ✔ |
| **13** | `CartTest` | **Test Case 13**: Verify Product quantity in Cart | **PASSED** ✔ |
| **14** | `CheckoutTest` | **Test Case 14**: Place Order: Register while Checkout | **PASSED** ✔ |
| **15** | `CheckoutTest` | **Test Case 15**: Place Order: Register before Checkout | **PASSED** ✔ |
| **16** | `CheckoutTest` | **Test Case 16**: Place Order: Login before Checkout | **PASSED** ✔ |
| **17** | `CartTest` | **Test Case 17**: Remove Products From Cart | **PASSED** ✔ |
| **18** | `ProductCatalogTest` | **Test Case 18**: View Category Products (Women, Men) | **PASSED** ✔ |
| **19** | `ProductCatalogTest` | **Test Case 19**: View & Cart Brand Products (Polo, Madame) | **PASSED** ✔ |
| **20** | `CartTest` | **Test Case 20**: Search Products and Verify Cart After Login | **PASSED** ✔ |
| **21** | `ProductCatalogTest` | **Test Case 21**: Add review on product | **PASSED** ✔ |
| **22** | `CartTest` | **Test Case 22**: Add to cart from Recommended items | **PASSED** ✔ |
| **23** | `CheckoutTest` | **Test Case 23**: Verify address details in checkout page | **PASSED** ✔ |
| **24** | `CheckoutTest` | **Test Case 24**: Download Invoice after purchase order | **PASSED** ✔ |
| **25** | `NavigationAndMiscTest` | **Test Case 25**: Verify Scroll Up using 'Arrow' button and Scroll Down | **PASSED** ✔ |
| **26** | `NavigationAndMiscTest` | **Test Case 26**: Verify Scroll Up without 'Arrow' button and Scroll Down | **PASSED** ✔ |

---

## 🏛️ Framework Architecture & Best Practices

- **Page Object Model (POM)**: Dedicated page classes for all application views with clear separation between page interactions and test logic.
- **Fluent Page Chaining**: Methods return the destination page object for fluent, readable test scripts.
- **ThreadLocal Driver Management**: Safe concurrent test execution in multi-browser and parallel configurations.
- **Network-Level Ad Blocking**: Headless and browser executions configure `--host-resolver-rules` to block Google Ad overlays (`*.googlesyndication.com`, `*.doubleclick.net`) to prevent intercepting popups and vignettes.
- **Allure TestNG Reporting**: Step annotations (`@Step`), test suites, features, epics, severities, and automatic failure screenshot attachments.
- **Data-Driven Testing (Apache POI)**: Clean extraction from Excel worksheets into TestNG DataProviders.

---

## 📁 Project Directory Structure

```
E-commerce-Checkout-Process-Automation
├── pom.xml                                # Maven configuration & plugins
├── docker-compose.yml                     # Selenium Grid 4 Hub + Chrome + Firefox
├── README.md                              # Complete documentation
└── src
    ├── main/java/com/ecommerce
    │   ├── actionbase/BasePage.java       # Centralized wait strategies, click, JS executor
    │   ├── constants/FrameworkConstants.java
    │   ├── drivers/                       # Driver, DriverFactory, DriverManager
    │   ├── enums/                         # BrowserType, ConfigProperties, RunMode, WaitStrategy
    │   ├── listeners/AllureListener.java  # Allure test lifecycle listener
    │   ├── pages/                         # Page Objects:
    │   │   ├── HomePage.java              # Home, categories, subscription, scroll
    │   │   ├── LoginPage.java             # Login & signup entry forms
    │   │   ├── SignupPage.java            # Account info, address form, account created/deleted
    │   │   ├── ProductsPage.java          # Product catalog, search, brands, modal cart
    │   │   ├── ProductDetailPage.java    # Product details, quantity, reviews
    │   │   ├── CartPage.java              # Shopping cart table, remove items, subscription
    │   │   ├── CheckoutPage.java          # Address details, comments, place order
    │   │   ├── PaymentPage.java           # Credit card details & confirm
    │   │   ├── OrderConfirmationPage.java # Order placed & invoice download
    │   │   ├── ContactUsPage.java         # Contact form & file upload
    │   │   └── TestCasesPage.java         # Official test cases catalog
    │   └── utils/                         # ConfigReader, DataProviderUtils, ExcelUtils, ScreenshotUtils
    └── test
        ├── java/com/ecommerce/tests/
        │   ├── BaseTest.java              # WebDriver setup & teardown
        │   ├── AuthenticationTest.java    # TC 1 to 5
        │   ├── ContactUsTest.java         # TC 6
        │   ├── NavigationAndMiscTest.java # TC 7, 10, 11, 25, 26
        │   ├── ProductCatalogTest.java    # TC 8, 9, 18, 19, 21
        │   ├── CartTest.java              # TC 12, 13, 17, 20, 22
        │   ├── CheckoutTest.java          # TC 14, 15, 16, 23, 24
        │   ├── DataDrivenCheckoutTest.java# Excel data-driven tests
        │   └── FrameworkUnitTest.java     # Config & Excel unit tests
        └── resources/
            ├── config/config.properties
            ├── testdata/testdata.xlsx
            ├── testdata/sample.txt
            └── testng.xml                 # Full 26-test suite
```

---

## 🚀 Execution Guide

### 1. Local Execution
```bash
cd /Users/shraddhamali/AutomationTesting/AutomationProjects/E-commerce-Checkout-Process-Automation

# Execute the complete 26-test suite
mvn clean test

# Execute specific suite classes
mvn test -Dtest=AuthenticationTest
mvn test -Dtest=ContactUsTest
mvn test -Dtest=NavigationAndMiscTest
mvn test -Dtest=ProductCatalogTest
mvn test -Dtest=CartTest
mvn test -Dtest=CheckoutTest

# View Allure interactive report
mvn allure:serve
```

### 2. Selenium Grid 4 (Docker)
For local distributed testing with Docker containers, refer to the [Selenium Grid Guide](SELENIUM_GRID.md):
```bash
./grid.sh start     # Start Grid Hub + Chrome + Firefox nodes
./grid.sh test      # Run tests on Grid
./grid.sh stop      # Stop Grid
```

### 3. Cloud Testing (BrowserStack & Sauce Labs)
For cloud cross-browser testing with video recordings, refer to the [Cloud Testing Guide](CLOUD_TESTING.md):
```bash
# BrowserStack Automate
./bstack.sh check               # Verify credentials
./bstack.sh test ContactUsTest  # Run single test
./bstack.sh cloud               # Run parallel cross-browser suite
./bstack.sh open                # Open BrowserStack live dashboard

# Sauce Labs Cloud
./sauce.sh check                # Verify credentials
./sauce.sh test ContactUsTest   # Run single test
./sauce.sh all                  # Run full suite
./sauce.sh open                 # Open Sauce Labs dashboard
```

### 4. CI/CD & Test Data Architecture
* **Enterprise Test Data & Security**: Refer to the [Test Data & Security Guide](TEST_DATA_ARCHITECTURE.md) for dynamic Datafaker factories, Jackson JSON models, and AES-256 password encryption.
* **Jenkins Parameterized Pipeline**: See [Jenkinsfile](Jenkinsfile) supporting Local, Grid, BrowserStack, and Sauce Labs with Safari and Docker management.
* **GitHub Actions Workflow**: See [.github/workflows/e2e-automation.yml](.github/workflows/e2e-automation.yml) with secret injection and Allure artifact uploads.

