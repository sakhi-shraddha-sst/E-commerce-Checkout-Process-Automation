# Enterprise Multi-Tiered Test Data & Security Architecture Guide

[![Datafaker](https://img.shields.io/badge/Datafaker-2.2.2-blue.svg)](https://www.datafaker.net/)
[![Jackson](https://img.shields.io/badge/Jackson-2.17.2-orange.svg)](https://github.com/FasterXML/jackson)
[![AES-256](https://img.shields.io/badge/Security-AES--256_GCM-red.svg)](https://en.wikipedia.org/wiki/Advanced_Encryption_Standard)
[![Jenkins](https://img.shields.io/badge/Jenkins-Pipeline-darkblue.svg)](https://www.jenkins.io/)
[![GitHub Actions](https://img.shields.io/badge/CI%2FCD-GitHub_Actions-black.svg)](https://github.com/features/actions)

This document details the **enterprise test data management (TDM) and secrets security architecture** implemented in this framework.

---

## 📑 Table of Contents

1. [Architectural Overview: Multi-Tiered Data Engine](#-architectural-overview-multi-tiered-data-engine)
2. [Data Strategy Comparison: Excel vs. Datafaker vs. JSON vs. API](#-data-strategy-comparison)
3. [Dynamic Test Data Generation (Datafaker)](#-dynamic-test-data-generation-datafaker)
4. [JSON-to-POJO Scenario Models (Jackson)](#-json-to-pojo-scenario-models-jackson)
5. [Password & Sensitive Credentials Security (AES-256 GCM)](#-password--sensitive-credentials-security)
6. [CI/CD Pipeline Integration (Jenkins & GitHub Actions)](#-cicd-pipeline-integration)
7. [Enterprise Interview Master Cheat Sheet](#-enterprise-interview-master-cheat-sheet)

---

## 🌟 Architectural Overview: Multi-Tiered Data Engine

In modern test automation, a single data source is insufficient. The framework utilizes a **hybrid 4-tier model**:

```
┌──────────────────────────────────────────────────────────────────────────────────┐
│                   TIER 1: REUSABLE CORE ENGINE (Never Changes)                  │
│                                                                                  │
│  • drivers/     : Driver, DriverManager, DriverFactory (ThreadLocal, Safari/All) │
│  • actionbase/  : BasePage (All explicit waits, JS execution, scroll, actions)   │
│  • utils/       :                                                                │
│      ├── TestDataFactory.java  : Datafaker generator (names, emails, cards)      │
│      ├── JsonUtils.java        : Jackson JSON-to-POJO generic mapper             │
│      ├── EncryptionUtils.java  : AES-256 GCM authenticated encryption/decryption │
│      ├── ConfigReader.java     : Priority: SysProps -> EnvVars -> Decrypted Props│
│      ├── ExcelUtils.java       : Apache POI reader (hybrid/client support)       │
│      └── ScreenshotUtils.java  : Byte array capture for Allure                   │
│  • listeners/   : AllureListener (Failure screenshots & Cloud session links)     │
└────────────────────────────────────────┬─────────────────────────────────────────┘
                                         │
                                         ▼
┌──────────────────────────────────────────────────────────────────────────────────┐
│               TIER 2: PROJECT-SPECIFIC LAYER (Unique to Application)             │
│                                                                                  │
│  • models/      : Strongly-typed DTOs (UserModel, CheckoutModel, ContactUsModel) │
│  • testdata/    : JSON Scenarios (users.json, checkout.json) + testdata.xlsx     │
│  • pages/       : Application Page Objects (HomePage, CartPage, CheckoutPage)    │
│  • tests/       : Tests consuming DataFactory & JSON models (Zero Hardcoding)    │
│  • config/      : config.properties (Sanitized: tokenized secrets, zero plain PW)│
│  • ci/cd/       : Jenkinsfile & .github/workflows/e2e-automation.yml             │
└──────────────────────────────────────────────────────────────────────────────────┘
```

---

## 📊 Data Strategy Comparison

| Approach | Ideal Use Case | Pros | Enterprise Limitations |
| :--- | :--- | :--- | :--- |
| **Datafaker Factory** | Registration, dynamic checkout, orders, unique forms | • Collision-free in parallel runs<br>• Realistic, randomized data<br>• Zero storage overhead | Not suited for fixed deterministic edge-case accounts |
| **JSON-to-POJO** | Pre-configured scenarios, boundary tests, card formats | • Clean Git diffs (no binary conflicts)<br>• Strongly-typed with compiler safety<br>• Easy environment grouping | Static files can become stale if backend database resets |
| **Environment / Secrets** | Passwords, Cloud access keys, database URLs | • Zero plaintext credentials in Git<br>• Compliant with SOC 2 / ISO 27001 | Requires CI/CD or local vault configuration |
| **Excel (Apache POI)** | Client reporting, manual QA collaboration | • Friendly for non-technical stakeholders<br>• Tabular overview | Binary merge conflicts; file locking in parallel runs |
| **API Pre-seeding** | Complex prerequisite state (e.g. user with active cart) | • 10x faster than UI navigation<br>• 100% reliable state setup | Requires access to backend service APIs |

---

## 🎲 Dynamic Test Data Generation (Datafaker)

Located in: [`TestDataFactory.java`](src/main/java/com/ecommerce/utils/TestDataFactory.java)

### Generating Randomized Users On-the-Fly
```java
// Completely unique user with random name, address, phone, and safe password
UserModel user = TestDataFactory.generateRandomUser();

loginPage.enterSignupDetails(user.getName(), user.getEmail());
signupPage.fillAccountDetails(user.getPassword(), user.getDay(), user.getMonth(), user.getYear());
signupPage.fillAddressDetails(
    user.getFirstName(), user.getLastName(), user.getCompany(),
    user.getAddress(), user.getAddress2(), user.getCountry(),
    user.getState(), user.getCity(), user.getZipcode(), user.getMobile()
);
```

### Generating Dynamic Payment Data
```java
CheckoutModel payment = TestDataFactory.generatePaymentData();

OrderConfirmationPage confirmationPage = paymentPage.payAndConfirm(
    payment.getNameOnCard(), payment.getCardNumber(), payment.getCvc(),
    payment.getExpiryMonth(), payment.getExpiryYear()
);
```

**Why this matters**: Because every email, username, and order is dynamically generated with unique timestamps and random numbers, **multiple test threads running in parallel (locally, on Selenium Grid, or in BrowserStack) never encounter "Email already registered" or account collision errors!**

---

## 📄 JSON-to-POJO Scenario Models (Jackson)

Located in: [`JsonUtils.java`](src/main/java/com/ecommerce/utils/JsonUtils.java) and [`models/`](src/main/java/com/ecommerce/models/)

For deterministic scenarios (e.g. specific Visa card testing), data is stored in version-controlled JSON files:

* [`src/test/resources/testdata/checkout.json`](src/test/resources/testdata/checkout.json)
* [`src/test/resources/testdata/users.json`](src/test/resources/testdata/users.json)

### Loading Strongly-Typed POJOs
```java
CheckoutModel checkout = JsonUtils.deserialize(
    "src/test/resources/testdata/checkout.json", 
    CheckoutModel.class
);

LOGGER.info("Using Card: {}", checkout.getCardNumber());
```

---

## 🔐 Password & Sensitive Credentials Security

### The Defense-in-Depth Model:

1. **No Plaintext Passwords in Git**:
   - Live passwords and cloud tokens must NEVER be committed to Git in plaintext.
2. **Environment Variables & Secrets Precedence**:
   - In [`ConfigReader.java`](src/main/java/com/ecommerce/utils/ConfigReader.java), the lookup hierarchy is:
     1. **System Property** (`-Dkey=value`)
     2. **Environment Variable** (`System.getenv("KEY")`)
     3. **Decrypted Value from `config.properties`** (if prefixed with `ENC(...)`)
     4. Default fallback
3. **AES-256 GCM Authenticated Encryption**:
   - Located in: [`EncryptionUtils.java`](src/main/java/com/ecommerce/utils/EncryptionUtils.java)
   - Encrypts strings with standard AES-256 GCM and prepends `ENC(...)`.
   - The master key is read securely from `System.getenv("APP_MASTER_KEY")`.

### How to Encrypt a New Secret via CLI:
```bash
# Encrypt any password or access key:
java -cp target/classes:target/test-classes:... com.ecommerce.utils.EncryptionUtils "MySecretPassword"

# Output:
# Original  : MySecretPassword
# Encrypted : ENC(Xy8a...==)
```
Place `ENC(...)` into `config.properties` or Excel. `ConfigReader` will automatically decrypt it in-memory at runtime!

---

## 🚀 CI/CD Pipeline Integration

### 1. Parameterized Jenkins Pipeline (`Jenkinsfile`)
Located at root: [`Jenkinsfile`](Jenkinsfile)

* **Supported Parameters**:
  - `RUN_MODE`: `local`, `grid`, `browserstack`, `saucelabs`
  - `BROWSER`: `chrome`, `firefox`, `edge`, `safari`
  - `SUITE_FILE`: `testng.xml`, `testng-cloud.xml`, `testng-crossbrowser.xml`
  - `HEADLESS`: `true`, `false`
  - `TEST_FILTER`: Optional single test class (e.g. `CheckoutTest`)
* **Jenkins Credentials Store Binding**:
  Credentials are bound via `withCredentials`:
  - `BROWSERSTACK_CREDENTIALS` ➔ `BROWSERSTACK_USERNAME`, `BROWSERSTACK_ACCESSKEY`
  - `SAUCELABS_CREDENTIALS` ➔ `SAUCELABS_USERNAME`, `SAUCELABS_ACCESSKEY`
  - `TEST_USER_CREDENTIALS` ➔ `TEST_USER_EMAIL`, `TEST_USER_PASSWORD`
  - `APP_MASTER_KEY` ➔ Master secret for AES-256 decryption
* **Docker Lifecycle**: Automatically spins up `docker-compose.yml` if `RUN_MODE == 'grid'` and shuts it down in `cleanup`.
* **Allure Report**: Automatically published via Jenkins Allure Plugin.

### 2. GitHub Actions Workflow (`.github/workflows/e2e-automation.yml`)
Located in: [`.github/workflows/e2e-automation.yml`](.github/workflows/e2e-automation.yml)

* **Triggers**: `push`, `pull_request`, and manual `workflow_dispatch` with parameter dropdowns.
* **OS Routing**: Runs on `macos-latest` when `safari` is selected, and `ubuntu-latest` for Linux browsers.
* **Secrets Injected via GitHub Repository Secrets**:
  - `BROWSERSTACK_USERNAME` & `BROWSERSTACK_ACCESSKEY`
  - `SAUCELABS_USERNAME` & `SAUCELABS_ACCESSKEY`
  - `TEST_USER_PASSWORD` & `APP_MASTER_KEY`

---

## 💡 Enterprise Interview Master Cheat Sheet

When asked: **"How do you manage test data and secure passwords in your automation framework?"**

> 1. *"We implement a **multi-tiered test data architecture** rather than relying solely on static Excel files.*
> 2. *For high-concurrency parallel runs, we use **Dynamic Data Factories (Datafaker)** to generate unique, realistic user profiles, addresses, and credit cards on-the-fly. This completely prevents duplicate email and account collision failures in CI/CD.*
> 3. *For deterministic scenarios and edge cases, we use **Jackson JSON-to-POJO models**, which are version-controlled, strongly typed, and eliminate binary Git merge conflicts.*
> 4. *For **password and credentials protection**, we follow a zero-plaintext policy. In CI/CD pipelines (Jenkins / GitHub Actions), credentials are bound at runtime via **Secret Stores and Environment Variables**. For any credentials stored in configuration files, we use an in-house **AES-256 GCM encryption utility** (`EncryptionUtils`) with `ENC(...)` tokens decrypted in-memory using an environment master key.*
> 5. *For client reporting and non-technical QA teams, we retain **Apache POI Excel DataProviders** as a hybrid option."*
