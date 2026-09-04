# Selenium Grid 4 with Docker Guide

This guide provides complete technical documentation on setting up, managing, scaling, and running automated tests against **Selenium Grid 4** using Docker in this project.

---

## 📑 Table of Contents

1. [Overview & Architecture](#-overview--architecture)
2. [Docker Compose Architecture](#-docker-compose-architecture)
3. [Apple Silicon (M1/M2/M3/M4) Compatibility](#-apple-silicon-m1m2m3m4-compatibility)
4. [One-Click Management Bash Scripts](#-one-click-management-bash-scripts)
5. [Direct Docker Commands Reference](#-direct-docker-commands-reference)
6. [Selenium Grid Web Console & Live VNC Monitoring](#-selenium-grid-web-console--live-vnc-monitoring)
7. [Running Tests Against Selenium Grid](#-running-tests-against-selenium-grid)
8. [Generating Allure Reports for Grid Tests](#-generating-allure-reports-for-grid-tests)
9. [Scaling Browser Nodes Horizontally](#-scaling-browser-nodes-horizontally)
10. [Framework Integration Details](#-framework-integration-details)
11. [Troubleshooting & FAQ](#-troubleshooting--faq)

---

## 🌟 Overview & Architecture

**Selenium Grid 4** allows distributed and concurrent execution of WebDriver tests across multiple machines and browser environments.

```
                  +----------------------------------------------+
                  |               Selenium Grid Hub              |
                  |                (Port 4444)                   |
                  |  [Router] -> [Distributor] -> [Session Queue]|
                  |              [Event Bus 4442/4443]           |
                  +----------------------+-----------------------+
                                         |
                     +-------------------+-------------------+
                     |                                       |
       +-------------v-------------+           +-------------v-------------+
       |        Chrome Node        |           |       Firefox Node        |
       |  (selenium/node-chrome)   |           |  (selenium/node-firefox)  |
       |  4 Concurrent Sessions    |           |  4 Concurrent Sessions    |
       |  Google Chrome v127       |           |  Mozilla Firefox v128     |
       +---------------------------+           +---------------------------+
```

### Components:
- **Router**: The public entrypoint (`http://localhost:4444`) redirecting requests to Distributor or Nodes.
- **Distributor**: Tracks registered nodes and assigns new sessions to the node with available capacity.
- **Session Queue**: Holds incoming session creation requests when all browser slots are occupied.
- **Event Bus**: Internal communication channel between the Hub and Nodes (ports `4442` & `4443`).
- **Nodes**: Isolated Docker containers hosting browser binaries (`google-chrome`, `firefox`) and WebDriver binaries (`chromedriver`, `geckodriver`).

---

## 🐳 Docker Compose Architecture

The grid is defined in [`docker-compose.yml`](docker-compose.yml):

```yaml
services:
  selenium-hub:
    image: selenium/hub:4.23.0
    platform: linux/amd64
    container_name: selenium-hub
    ports:
      - "4442:4442" # Event Bus publish port
      - "4443:4443" # Event Bus subscribe port
      - "4444:4444" # Hub REST API & Web UI
    environment:
      - GRID_MAX_SESSION=16
      - GRID_TIMEOUT=300

  chrome-node:
    image: selenium/node-chrome:4.23.0
    platform: linux/amd64
    container_name: chrome-node
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=4

  firefox-node:
    image: selenium/node-firefox:4.23.0
    platform: linux/amd64
    container_name: firefox-node
    shm_size: 2gb
    depends_on:
      - selenium-hub
    environment:
      - SE_EVENT_BUS_HOST=selenium-hub
      - SE_EVENT_BUS_PUBLISH_PORT=4442
      - SE_EVENT_BUS_SUBSCRIBE_PORT=4443
      - SE_NODE_MAX_SESSIONS=4
```

### Critical Parameters:
- **`shm_size: 2gb`**: Essential for headless Chrome and Firefox inside Docker. Prevents browser crashes caused by Docker's default tiny 64MB `/dev/shm` shared memory partition.
- **`SE_NODE_MAX_SESSIONS=4`**: Each node container can host up to 4 parallel browser instances simultaneously.

---

## 🍎 Apple Silicon (M1/M2/M3/M4) Compatibility

Official `selenium/node-chrome` and `selenium/node-firefox` images are compiled for `linux/amd64`. On ARM64 Macs, running without specifying the platform causes:
```text
no matching manifest for linux/arm64/v8 in the manifest list entries: not found
```

### Solution:
Every service in `docker-compose.yml` specifies:
```yaml
platform: linux/amd64
```
Docker Desktop uses macOS Rosetta 2 / QEMU virtualization to execute the x86_64 containers seamlessly with near-native performance.

---

## 🚀 One-Click Management Bash Scripts

For convenience, executable shell scripts are provided at the project root:

### 1. Main Management Tool: `grid.sh`

```bash
# Display help and all available commands
./grid.sh help

# Start Hub, Chrome Node, and Firefox Node (waits until healthy)
./grid.sh start

# Check Grid status, active nodes, and browser slots
./grid.sh status

# Open the Grid Web Console in your browser
./grid.sh open-ui

# Run the testng-grid.xml test suite against the Grid
./grid.sh run-tests

# Generate and view interactive Allure test report
./grid.sh report

# Scale Chrome nodes to 3 containers (12 concurrent sessions)
./grid.sh scale chrome 3

# View live container logs
./grid.sh logs
./grid.sh logs chrome-node
./grid.sh logs firefox-node

# Stop and tear down all Grid containers
./grid.sh stop

# Restart Grid services
./grid.sh restart
```

### 2. Standalone Shortcut Scripts

| Script | Action |
| :--- | :--- |
| `./grid-start.sh` | Starts Selenium Hub + Chrome & Firefox nodes and verifies health |
| `./grid-status.sh` | Prints container status, registered nodes, and available slots |
| `./grid-stop.sh` | Gracefully stops and removes all grid containers |

---

## 💻 Direct Docker Commands Reference

If you prefer using Docker CLI directly:

```bash
# 1. Start all containers in background
docker compose up -d

# 2. View running containers
docker compose ps

# 3. Check hub status endpoint
curl -s http://localhost:4444/status | python3 -m json.tool

# 4. View container logs
docker compose logs -f
docker compose logs -f selenium-hub
docker compose logs -f chrome-node
docker compose logs -f firefox-node

# 5. Stop containers
docker compose down

# 6. Stop and remove orphan volumes
docker compose down -v
```

---

## 🌐 Selenium Grid Web Console & Live VNC Monitoring

### 1. Web Console UI
Once started, open:
👉 **[http://localhost:4444/ui/](http://localhost:4444/ui/)** *(or simply [http://localhost:4444](http://localhost:4444))*

> **Important**: Do **not** append `index.html` (e.g. `http://localhost:4444/ui/index.html`). Selenium Grid's React UI builds GraphQL endpoints dynamically via `pathname.replace('/ui/', '') + '/graphql'`, so appending `index.html` causes a malformed URL `http://localhost:4444index.html/graphql`.

The Web Console displays:
- Total number of registered nodes and available browser slots.
- Active test sessions, browser type, and duration.
- Queued requests awaiting available slots.

### 2. Live VNC Video Streaming
Each Selenium node has an integrated VNC server:
1. Open **[http://localhost:4444/ui/#/sessions](http://localhost:4444/ui/#/sessions)**
2. When a test is executing, click the **Video / Eye icon** next to the session.
3. Enter the default password:
   ```text
   secret
   ```
4. You will see real-time browser interaction inside the container!

---

## 🧪 Running Tests Against Selenium Grid

### 1. The Grid Suite XML ([`src/test/resources/testng-grid.xml`](src/test/resources/testng-grid.xml))

```xml
<?xml version="1.0" encoding="UTF-8"?>
<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="Selenium Grid Parallel Suite" parallel="tests" thread-count="2">

    <listeners>
        <listener class-name="com.ecommerce.listeners.AllureListener"/>
    </listeners>

    <!-- Chrome on Selenium Grid -->
    <test name="Selenium Grid - Chrome Node">
        <parameter name="browser" value="chrome"/>
        <parameter name="runMode" value="grid"/>
        <classes>
            <class name="com.ecommerce.tests.CheckoutTest"/>
        </classes>
    </test>

    <!-- Firefox on Selenium Grid -->
    <test name="Selenium Grid - Firefox Node">
        <parameter name="browser" value="firefox"/>
        <parameter name="runMode" value="grid"/>
        <classes>
            <class name="com.ecommerce.tests.CheckoutTest"/>
        </classes>
    </test>

</suite>
```

### 2. Execution Commands

```bash
# Option A: Using the management script (starts Grid automatically if down)
./grid.sh run-tests

# Option B: Using Maven directly
mvn test -DsuiteFile=src/test/resources/testng-grid.xml
```

---

## 📊 Generating Allure Reports for Grid Tests

Selenium Grid test executions are fully integrated with **Allure Reporting**. Every test executed via `testng-grid.xml` automatically captures detailed metadata, execution timelines, and failure screenshots:

### 1. Features in the Grid Allure Report
- **Suite Separation**: Allure separates tests into distinct suites:
  - **`Selenium Grid - Chrome Node`**
  - **`Selenium Grid - Firefox Node`**
- **Parameters Tagging**: Each test card explicitly logs parameters:
  - `runMode`: `grid`
  - `browser`: `chrome` or `firefox`
- **Screenshots on Failure**: When an assertion or timeout occurs inside a remote Docker container, `AllureListener` invokes `RemoteWebDriver.getScreenshotAs(BYTES)`. The screenshot is transferred from inside the container and attached directly as a PNG to the Allure report.
- **Parallel Timeline**: Visualizes simultaneous execution of Chrome and Firefox threads across Grid nodes.

### 2. View Interactive Allure Report (Recommended)
This starts a lightweight web server and opens the live interactive dashboard in your default browser:

```bash
# Option A: One-click with the helper script
./grid.sh report

# Option B: Direct Maven command
mvn allure:serve
```

### 3. Generate Standalone Offline HTML Report
To generate a static HTML folder (useful for CI/CD artifacts, Jenkins, GitHub Pages, or sharing):

```bash
# Generates report files in target/site/allure-maven-plugin/
mvn allure:report

# Open the static report in browser
open target/site/allure-maven-plugin/index.html
```

### 4. Clean and Re-run a Fresh Grid Cycle
To wipe old test runs and generate clean reports for a fresh test cycle:

```bash
# 1. Clean previous results and run Grid tests
mvn clean test -DsuiteFile=src/test/resources/testng-grid.xml

# 2. View the new report
./grid.sh report
```

---

## 📈 Scaling Browser Nodes Horizontally

To run larger parallel test batches, scale the node services:

```bash
# Scale Chrome node to 3 containers (3 * 4 = 12 Chrome slots)
./grid.sh scale chrome 3
# Or via docker compose:
docker compose up -d --scale chrome-node=3

# Scale Firefox node to 2 containers (2 * 4 = 8 Firefox slots)
./grid.sh scale firefox 2
# Or via docker compose:
docker compose up -d --scale firefox-node=2
```

Check the new slot count immediately with:
```bash
./grid.sh status
```

To revert back to single nodes:
```bash
docker compose up -d --scale chrome-node=1 --scale firefox-node=1
```

---

## ⚙️ Framework Integration Details

### How the Framework Routes to Grid:

1. **[`config.properties`](src/test/resources/config/config.properties)**:
   ```properties
   grid_url=http://localhost:4444/wd/hub
   ```

2. **[`DriverFactory.java`](src/main/java/com/ecommerce/drivers/DriverFactory.java)**:
   When `runMode` is `grid`, it initializes a `RemoteWebDriver`:
   ```java
   case GRID:
       return createRemoteGridDriver(browserType, isHeadless);
   ```
   ```java
   private static WebDriver createRemoteGridDriver(BrowserType browserType, boolean isHeadless) {
       String gridUrl = ConfigReader.get(ConfigProperties.GRID_URL);
       MutableCapabilities capabilities = switch (browserType) {
           case FIREFOX -> getFirefoxOptions(isHeadless);
           case EDGE -> getEdgeOptions(isHeadless);
           case CHROME -> getChromeOptions(isHeadless);
       };
       return new RemoteWebDriver(new URL(gridUrl), capabilities);
   }
   ```

3. **[`BaseTest.java`](src/test/java/com/ecommerce/tests/BaseTest.java)**:
   Captures `@Parameters({"browser", "runMode"})` from TestNG XML and routes it dynamically to `Driver.initDriver(browser, runMode)`.

---

## 🔧 Troubleshooting & FAQ

### Q1: `Connection refused` when running tests
- **Cause**: The Grid Hub container is not running.
- **Fix**: Run `./grid.sh start` and ensure Docker Desktop is open.

### Q2: Port 4444 is already in use
- **Cause**: Another service or a lingering container is holding port 4444.
- **Fix**:
  ```bash
  lsof -i :4444
  kill -9 <PID>
  # Then restart
  ./grid.sh restart
  ```

### Q3: `no matching manifest for linux/arm64` on M1/M2/M3/M4 Macs
- **Cause**: Attempting to pull images without AMD64 platform flag.
- **Fix**: Verify [`docker-compose.yml`](docker-compose.yml) includes `platform: linux/amd64` under each service (already configured in this repository).

### Q4: Browser crashes inside container during test
- **Cause**: Insufficient shared memory in container.
- **Fix**: Ensure `shm_size: 2gb` is present under the node service in `docker-compose.yml`.

### Q5: How to generate Allure Report after Grid tests?
```bash
mvn allure:serve
```

### Q6: `Failed to parse URL from http://localhost:4444index.html/graphql`
- **Cause**: Directly navigating to `http://localhost:4444/ui/index.html`. Selenium Grid 4's React UI builds GraphQL requests dynamically using `pathname.replace('/ui/', '') + '/graphql'`, resulting in a malformed URL when `index.html` is explicitly typed in the address bar.
- **Fix**: Open the clean URL without `index.html`:
  👉 **`http://localhost:4444/ui/`** or simply **`http://localhost:4444`** (or run `./grid.sh open-ui`).
