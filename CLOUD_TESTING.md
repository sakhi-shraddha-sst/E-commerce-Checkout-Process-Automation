# Cloud Testing Guide: BrowserStack & Sauce Labs

[![BrowserStack](https://img.shields.io/badge/BrowserStack-Automate-orange.svg)](https://www.browserstack.com/)
[![Sauce Labs](https://img.shields.io/badge/Sauce_Labs-Cloud-red.svg)](https://saucelabs.com/)
[![Selenium 4](https://img.shields.io/badge/Selenium-4.23.0-green.svg)](https://www.selenium.dev/)
[![Allure Report](https://img.shields.io/badge/Allure-2.24.0-yellow.svg)](https://qameta.io/allure-report/)

This guide provides technical documentation on running automated test suites on **BrowserStack Automate** and **Sauce Labs Cloud** in the E-Commerce Checkout Process Automation Framework.

---

## 📑 Table of Contents

1. [Architecture Overview](#-architecture-overview)
2. [Key Framework Capabilities](#-key-framework-capabilities)
3. [Configuration Setup (`config.properties`)](#-configuration-setup-configproperties)
4. [BrowserStack Automate Integration](#-browserstack-automate-integration)
   - [Credentials & Dashboard](#credentials--dashboard)
   - [BrowserStack CLI Runner (`bstack.sh`)](#browserstack-cli-runner-bstacksh)
   - [Watching Tests Live](#watching-tests-live)
5. [Sauce Labs Cloud Integration](#-sauce-labs-cloud-integration)
   - [Credentials & Regional Endpoints](#credentials--regional-endpoints)
   - [Sauce Labs CLI Runner (`sauce.sh`)](#sauce-labs-cli-runner-saucesh)
   - [Watching Tests Live](#watching-tests-live-1)
6. [Cross-Browser Cloud Parallel Execution (`testng-cloud.xml`)](#-cross-browser-cloud-parallel-execution-testng-cloudxml)
7. [Allure Report Integration with Cloud Session Links](#-allure-report-integration-with-cloud-session-links)
8. [CLI Commands Quick Reference](#-cli-commands-quick-reference)
9. [Troubleshooting & Common Issues](#-troubleshooting--common-issues)

---

## 🌟 Architecture Overview

The framework supports switching between **Local**, **Selenium Grid 4**, **BrowserStack**, and **Sauce Labs** with zero test code changes.

```
                           +-------------------------------------+
                           |         Maven Test Execution        |
                           |   mvn test -Drunmode=<provider>     |
                           +------------------+------------------+
                                              |
                             +----------------v---------------+
                             |    Driver / DriverFactory      |
                             |   Reads config.properties / CLI|
                             +----------------+---------------+
                                              |
         +------------------------------------+------------------------------------+
         |                                    |                                    |
+--------v-------------------+      +---------v------------------+       +---------v------------------+
|      Local Machine         |      |    BrowserStack Cloud      |       |      Sauce Labs Cloud      |
|  - ChromeDriver            |      |  - hub-cloud.browserstack  |       |  - ondemand.eu-central-1   |
|  - GeckoDriver             |      |  - macOS Sonoma / Win 11   |       |  - Windows 11 / Chrome     |
|  - EdgeDriver              |      |  - Real-time Video Stream  |       |  - Session Video & Logs    |
+----------------------------+      +----------------------------+       +----------------------------+
```

---

## 🚀 Key Framework Capabilities

### 1. Network-Level Google Ad & Vignette Blocking
E-commerce practice sites often load heavy third-party advertisements and full-screen interstitials (vignettes) that intercept WebDriver clicks. The framework configures `--host-resolver-rules` on Chrome options across both BrowserStack and Sauce Labs:
```java
chromeOptions.addArguments(
    "--host-resolver-rules=MAP *.googlesyndication.com 127.0.0.1, MAP *.google-analytics.com 127.0.0.1, MAP *.doubleclick.net 127.0.0.1, MAP *.googleadservices.com 127.0.0.1");
chromeOptions.addArguments("--disable-notifications");
chromeOptions.addArguments("--disable-popup-blocking");
```

### 2. Transparent Remote File Uploads (`LocalFileDetector`)
For test scenarios involving file attachments (such as `ContactUsTest`), tests running on cloud virtual machines (e.g. Windows in Germany) require streaming the file across the network. The framework registers Selenium's `LocalFileDetector`:
```java
if (driver instanceof RemoteWebDriver) {
    ((RemoteWebDriver) driver).setFileDetector(new LocalFileDetector());
}
```

### 3. W3C Compliant Capabilities
Modern cloud providers reject legacy JSON wire capabilities. Drivers are instantiated with standard W3C options:
* **BrowserStack**: `bstack:options` containing `os`, `osVersion`, `projectName`, `buildName`, and `sessionName`.
* **Sauce Labs**: `sauce:options` containing `build`, `name`, `username`, and `accessKey`.

---

## ⚙️ Configuration Setup (`config.properties`)

File location: [`src/test/resources/config/config.properties`](src/test/resources/config/config.properties)

```properties
# ==============================================================================
# Execution Mode: local, grid, browserstack, saucelabs
# ==============================================================================
runmode=local
browser=chrome

# ==============================================================================
# Cloud Services - BrowserStack Configuration
# ==============================================================================
browserstack_url=https://hub-cloud.browserstack.com/wd/hub
browserstack_username=shraddhathakur_dinjpo
browserstack_accesskey=8JrusaoqCT1G9LKFqnEP

# ==============================================================================
# Cloud Services - Sauce Labs Configuration
# ==============================================================================
# EU Central Endpoint (use ondemand.us-west-1.saucelabs.com:443/wd/hub for US)
saucelabs_url=https://ondemand.eu-central-1.saucelabs.com:443/wd/hub
saucelabs_username=oauth-shraddha.st.web-60361
saucelabs_accesskey=9b0608a1-c3ae-49cd-b2a2-9b004dc0c370
```

> [!TIP]
> Command-line `-D` arguments take highest priority over `config.properties`. For example: `mvn test -Drunmode=browserstack` will run on BrowserStack regardless of the `runmode` setting in the file.

---

## 🟠 BrowserStack Automate Integration

### Credentials & Dashboard
* **Sign Up / Login**: [https://automate.browserstack.com](https://automate.browserstack.com)
* **Access Keys**: Click **Access Key** at the top right of the dashboard.
* **Live Automate Dashboard**: [https://automate.browserstack.com/dashboard/v2](https://automate.browserstack.com/dashboard/v2)

### BrowserStack CLI Runner (`bstack.sh`)
The framework includes an executable CLI runner [`bstack.sh`](bstack.sh) for managing BrowserStack executions:

```bash
# 1. Verify credentials and API plan connectivity
./bstack.sh check

# 2. Run a specific test class (default: chrome)
./bstack.sh test ContactUsTest

# 3. Run a test with Firefox or Edge
./bstack.sh test CartTest firefox
./bstack.sh test AuthenticationTest edge

# 4. Run the dedicated cloud cross-browser parallel suite
./bstack.sh cloud

# 5. Run the complete 26-test suite on BrowserStack
./bstack.sh all chrome

# 6. Open the BrowserStack live dashboard in your browser
./bstack.sh open

# 7. Generate and serve the Allure HTML report
./bstack.sh report
```

### Watching Tests Live
1. Run `./bstack.sh test ContactUsTest`.
2. Open [https://automate.browserstack.com/dashboard/v2](https://automate.browserstack.com/dashboard/v2) (or run `./bstack.sh open`).
3. Click on the active session under project **`E-Commerce Checkout Automation`** to watch the real-time video stream as the test executes.

---

## 🔴 Sauce Labs Cloud Integration

### Credentials & Regional Endpoints
* **Dashboard (EU)**: [https://app.eu-central-1.saucelabs.com](https://app.eu-central-1.saucelabs.com)
* **Dashboard (US)**: [https://app.saucelabs.com](https://app.saucelabs.com)
* **Access Keys**: Avatar (top right) > **User Settings** > **Access Configuration**.

| Region | Web Hub Endpoint | Web Dashboard |
| :--- | :--- | :--- |
| **EU Central (Default)** | `https://ondemand.eu-central-1.saucelabs.com:443/wd/hub` | `https://app.eu-central-1.saucelabs.com` |
| **US West** | `https://ondemand.us-west-1.saucelabs.com:443/wd/hub` | `https://app.saucelabs.com` |

> [!WARNING]
> Do NOT use `app.saucelabs.com` as the `saucelabs_url`. The `app.` domain is for the human web UI and returns HTML. The Selenium hub requires `ondemand.`.

### Sauce Labs CLI Runner (`sauce.sh`)
The framework includes [`sauce.sh`](sauce.sh) for managing Sauce Labs test runs:

```bash
# 1. Verify credentials and endpoint operational status
./sauce.sh check

# 2. Run a single test (default: chrome)
./sauce.sh test ContactUsTest

# 3. Run cross-browser tests
./sauce.sh test CartTest firefox
./sauce.sh test CheckoutTest edge

# 4. Run the entire 26-test suite on Sauce Labs
./sauce.sh all chrome

# 5. Open Sauce Labs live dashboard
./sauce.sh open

# 6. Generate and view Allure Report
./sauce.sh report
```

### Watching Tests Live
1. Run `./sauce.sh test ContactUsTest`.
2. Open [https://app.eu-central-1.saucelabs.com/dashboard/builds](https://app.eu-central-1.saucelabs.com/dashboard/builds).
3. Under **Automated** > **Test Results**, click the live session to see the video recording and command execution waterfall.

---

## ⚡ Cross-Browser Cloud Parallel Execution (`testng-cloud.xml`)

File location: [`src/test/resources/testng-cloud.xml`](src/test/resources/testng-cloud.xml)

This suite runs tests concurrently across different browsers in the cloud:
```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Cloud Cross-Browser Suite" parallel="tests" thread-count="2">

    <listeners>
        <listener class-name="com.ecommerce.listeners.AllureListener"/>
    </listeners>

    <!-- Chrome on Cloud -->
    <test name="Cloud Test - Chrome">
        <parameter name="browser" value="chrome"/>
        <parameter name="runMode" value="browserstack"/>
        <classes>
            <class name="com.ecommerce.tests.CheckoutTest"/>
        </classes>
    </test>

    <!-- Firefox on Cloud -->
    <test name="Cloud Test - Firefox">
        <parameter name="browser" value="firefox"/>
        <parameter name="runMode" value="browserstack"/>
        <classes>
            <class name="com.ecommerce.tests.CheckoutTest"/>
        </classes>
    </test>

</suite>
```

Run this suite on BrowserStack:
```bash
./bstack.sh cloud
# or
mvn test -DsuiteFile=src/test/resources/testng-cloud.xml -Drunmode=browserstack
```

---

## 📊 Allure Report Integration with Cloud Session Links

When executing on BrowserStack or Sauce Labs, [`AllureListener.java`](src/main/java/com/ecommerce/listeners/AllureListener.java) and [`BaseTest.java`](src/test/java/com/ecommerce/tests/BaseTest.java) capture the active `SessionId` from `RemoteWebDriver` and embed direct session links into the Allure report:

```
+-------------------------------------------------------------+
|                     Allure Test Overview                    |
|                                                             |
|  [Test Case 6: Contact Us Form]                  PASSED ✔  |
|  Duration: 36.7s                                            |
|                                                             |
|  Attachments:                                               |
|  ---------------------------------------------------------  |
|  🎥 Sauce Labs Video & Logs                                 |
|  [Open Video Replay & Logs (920ffa4df6aa45d5a758cf45f1...)] |
+-------------------------------------------------------------+
```

To view the generated report with embedded cloud video links:
```bash
mvn allure:serve
```

---

## 📋 CLI Commands Quick Reference

| Action | BrowserStack Command | Sauce Labs Command |
| :--- | :--- | :--- |
| **Verify Connection** | `./bstack.sh check` | `./sauce.sh check` |
| **Run Single Test (Chrome)** | `./bstack.sh test ContactUsTest` | `./sauce.sh test ContactUsTest` |
| **Run Single Test (Firefox)** | `./bstack.sh test CartTest firefox` | `./sauce.sh test CartTest firefox` |
| **Run Single Test (Edge)** | `./bstack.sh test AuthenticationTest edge` | `./sauce.sh test AuthenticationTest edge` |
| **Run Full 26-Test Suite** | `./bstack.sh all chrome` | `./sauce.sh all chrome` |
| **Run Cross-Browser Suite** | `./bstack.sh cloud` | `mvn test -DsuiteFile=src/test/resources/testng-cloud.xml -Drunmode=saucelabs` |
| **Open Cloud Dashboard** | `./bstack.sh open` | `./sauce.sh open` |
| **Serve Allure Report** | `./bstack.sh report` | `./sauce.sh report` |

---

## 🛠️ Troubleshooting & Common Issues

### 1. `Response code 200, Content-Type: text/html... Expected JSON`
* **Root Cause**: The URL in `config.properties` has `app.` instead of `ondemand.` or `hub-cloud.`.
* **Fix**: Ensure your endpoint is `https://ondemand.eu-central-1.saucelabs.com:443/wd/hub` (for Sauce Labs) or `https://hub-cloud.browserstack.com/wd/hub` (for BrowserStack).

### 2. `Authentication failed: Misconfigured -- Invalid credentials` (401 Unauthorized)
* **Root Cause**: Invalid username or access key, or region mismatch (e.g., using EU credentials on a US endpoint).
* **Fix**: Run `./sauce.sh check` or `./bstack.sh check` to pinpoint the authentication failure.

### 3. `InvalidArgument: File not found : /Users/.../sample.txt` on Cloud
* **Root Cause**: The browser is running on a remote cloud machine (e.g. Windows) and cannot access your local file system path.
* **Fix**: The framework registers `LocalFileDetector` on `RemoteWebDriver` in [`Driver.java`](src/main/java/com/ecommerce/drivers/Driver.java) so files are automatically streamed to the remote browser.

### 4. Tests running locally instead of Cloud
* **Root Cause**: Missing `-Drunmode=saucelabs` or `-Drunmode=browserstack` on the Maven command line.
* **Fix**: Use `./bstack.sh test <TestName>` or `./sauce.sh test <TestName>`, which automatically pass the correct `-Drunmode` flags.
