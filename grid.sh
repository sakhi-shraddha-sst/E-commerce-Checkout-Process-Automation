#!/usr/bin/env bash

# ==============================================================================
# Selenium Grid Management CLI
# E-commerce Checkout Process Automation Framework
# ==============================================================================

set -e

# Color definitions for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

COMPOSE_FILE="docker-compose.yml"
GRID_URL="http://localhost:4444"
STATUS_URL="${GRID_URL}/status"
UI_URL="${GRID_URL}/ui/"

# Print banner
print_banner() {
    echo -e "${CYAN}${BOLD}"
    echo "=========================================================="
    echo "       SELENIUM GRID 4 - DOCKER MANAGEMENT UTILITY        "
    echo "=========================================================="
    echo -e "${NC}"
}

# Check if Docker is installed and daemon is running
check_docker() {
    if ! command -v docker &> /dev/null; then
        echo -e "${RED}[ERROR] Docker is not installed or not in your PATH.${NC}"
        echo "Please install Docker Desktop from https://www.docker.com/products/docker-desktop/"
        exit 1
    fi

    if ! docker info &> /dev/null; then
        echo -e "${RED}[ERROR] Docker daemon is not running.${NC}"
        echo "Please launch Docker Desktop application and wait until it is ready."
        exit 1
    fi
}

# Start Selenium Grid
start_grid() {
    print_banner
    check_docker
    echo -e "${BLUE}[INFO] Starting Selenium Grid Hub and Nodes...${NC}"

    docker compose -f "$COMPOSE_FILE" up -d

    echo -e "${YELLOW}[INFO] Waiting for Selenium Grid to initialize...${NC}"
    local retries=30
    local count=0
    local ready=false

    while [ $count -lt $retries ]; do
        if curl -s "$STATUS_URL" 2>/dev/null | grep -q '"ready": true'; then
            ready=true
            break
        elif curl -s "$STATUS_URL" 2>/dev/null | grep -q '"ready": false'; then
            echo -e "${YELLOW}  -> Hub ready, waiting for browser nodes to register... ($((count + 1))/$retries)${NC}"
        else
            echo -e "${YELLOW}  -> Waiting for Grid Hub endpoint at ${GRID_URL}... ($((count + 1))/$retries)${NC}"
        fi
        sleep 2
        count=$((count + 1))
    done

    if [ "$ready" = true ]; then
        echo -e "\n${GREEN}${BOLD}✔ Selenium Grid is READY and healthy!${NC}"
        echo -e "${CYAN}----------------------------------------------------------${NC}"
        echo -e "  ${BOLD}Grid Console UI:${NC}    ${CYAN}${UI_URL}${NC}"
        echo -e "  ${BOLD}Status Endpoint:${NC}    ${CYAN}${STATUS_URL}${NC}"
        echo -e "  ${BOLD}Hub Remote URL:${NC}     ${CYAN}${GRID_URL}/wd/hub${NC}"
        echo -e "${CYAN}----------------------------------------------------------${NC}"
        status_grid_summary
    else
        echo -e "\n${RED}[WARN] Selenium Grid started, but nodes did not become fully ready within 60s.${NC}"
        echo "Run './grid.sh logs' to inspect startup logs."
    fi
}

# Stop Selenium Grid
stop_grid() {
    print_banner
    check_docker
    echo -e "${YELLOW}[INFO] Stopping and removing Selenium Grid containers...${NC}"
    docker compose -f "$COMPOSE_FILE" down
    echo -e "${GREEN}${BOLD}✔ Selenium Grid stopped successfully.${NC}"
}

# Restart Selenium Grid
restart_grid() {
    print_banner
    check_docker
    echo -e "${YELLOW}[INFO] Restarting Selenium Grid...${NC}"
    docker compose -f "$COMPOSE_FILE" down
    start_grid
}

# Display detailed status of Grid
status_grid() {
    print_banner
    check_docker

    if ! curl -s "$STATUS_URL" &> /dev/null; then
        echo -e "${RED}● Selenium Grid is NOT running (connection refused on ${GRID_URL}).${NC}"
        echo -e "Start the grid with: ${CYAN}./grid.sh start${NC}"
        return 1
    fi

    echo -e "${GREEN}● Selenium Grid is RUNNING at ${GRID_URL}${NC}\n"
    echo -e "${BOLD}Running Docker Containers:${NC}"
    docker compose -f "$COMPOSE_FILE" ps

    echo -e "\n${BOLD}Registered Grid Nodes & Slots:${NC}"
    if command -v python3 &> /dev/null; then
        curl -s "$STATUS_URL" | python3 -c '
import sys, json
try:
    data = json.load(sys.stdin)
    val = data.get("value", {})
    ready = val.get("ready", False)
    nodes = val.get("nodes", [])
    print(f"  Grid Overall Ready: {ready}")
    print(f"  Total Registered Nodes: {len(nodes)}")
    for i, node in enumerate(nodes, 1):
        uri = node.get("uri")
        max_sess = node.get("maxSessions")
        avail = node.get("availability")
        slots = node.get("slots", [])
        browsers = {}
        for s in slots:
            bname = s.get("stereotype", {}).get("browserName", "unknown")
            browsers[bname] = browsers.get(bname, 0) + 1
        b_summary = ", ".join([f"{k}: {v} slots" for k, v in browsers.items()])
        print(f"  [Node {i}] Status: {avail} | Max Sessions: {max_sess} | URI: {uri} | {b_summary}")
except Exception as e:
    print("  Could not parse detailed node information:", e)
'
    else
        curl -s "$STATUS_URL"
    fi

    echo -e "\n${CYAN}Open Grid Web UI at: ${BOLD}${UI_URL}${NC}"
}

# Brief status summary used after start
status_grid_summary() {
    if command -v python3 &> /dev/null; then
        curl -s "$STATUS_URL" | python3 -c '
import sys, json
try:
    data = json.load(sys.stdin)
    nodes = data.get("value", {}).get("nodes", [])
    for node in nodes:
        slots = node.get("slots", [])
        if slots:
            bname = slots[0].get("stereotype", {}).get("browserName", "node")
            print(f"  ✔ Node {bname.capitalize()}: {len(slots)} available slots")
except Exception:
    pass
'
    fi
}

# View container logs
logs_grid() {
    check_docker
    local service="$1"
    if [ -n "$service" ]; then
        echo -e "${BLUE}[INFO] Tailing logs for service: ${service}${NC}"
        docker compose -f "$COMPOSE_FILE" logs -f "$service"
    else
        echo -e "${BLUE}[INFO] Tailing logs for all Selenium Grid containers (Ctrl+C to exit)...${NC}"
        docker compose -f "$COMPOSE_FILE" logs -f
    fi
}

# Scale browser nodes
scale_grid() {
    local browser="$1"
    local count="$2"

    if [ -z "$browser" ] || [ -z "$count" ]; then
        echo -e "${RED}[ERROR] Missing arguments for scale.${NC}"
        echo "Usage: ./grid.sh scale <chrome|firefox> <number_of_nodes>"
        echo "Example: ./grid.sh scale chrome 3"
        exit 1
    fi

    check_docker
    local service="${browser}-node"
    echo -e "${BLUE}[INFO] Scaling service '${service}' to ${count} instances...${NC}"
    docker compose -f "$COMPOSE_FILE" up -d --scale "${service}=${count}"
    echo -e "${GREEN}${BOLD}✔ Successfully scaled ${service} to ${count}.${NC}"
    sleep 3
    status_grid
}

# Open Grid UI in default browser
open_ui() {
    if ! curl -s "$STATUS_URL" &> /dev/null; then
        echo -e "${YELLOW}[WARN] Grid is not running. Starting grid first...${NC}"
        start_grid
    fi

    echo -e "${BLUE}[INFO] Opening Selenium Grid UI in browser: ${UI_URL}${NC}"
    if [[ "$OSTYPE" == "darwin"* ]]; then
        open "$UI_URL"
    elif [[ "$OSTYPE" == "linux-gnu"* ]]; then
        xdg-open "$UI_URL" &> /dev/null || sensible-browser "$UI_URL" &> /dev/null || echo "Please open ${UI_URL}"
    else
        echo "Please open ${UI_URL} in your browser."
    fi
}

# Run the testng-grid.xml test suite
run_tests() {
    print_banner
    if ! curl -s "$STATUS_URL" &> /dev/null; then
        echo -e "${YELLOW}[WARN] Selenium Grid is not running. Starting grid before running tests...${NC}"
        start_grid
    fi

    echo -e "${BLUE}[INFO] Executing TestNG Grid Suite against Selenium Grid Hub (${GRID_URL})...${NC}"
    mvn test -DsuiteFile=src/test/resources/testng-grid.xml
}

# Generate and open Allure report
open_report() {
    print_banner
    echo -e "${BLUE}[INFO] Generating and serving interactive Allure report...${NC}"
    mvn allure:serve
}

# Display help menu
show_help() {
    print_banner
    echo -e "${BOLD}Usage:${NC} ./grid.sh <command> [arguments]"
    echo ""
    echo -e "${BOLD}Available Commands:${NC}"
    echo -e "  ${CYAN}start | up${NC}            Start Selenium Grid Hub, Chrome Node, and Firefox Node"
    echo -e "  ${CYAN}stop | down${NC}           Stop and remove all Selenium Grid containers"
    echo -e "  ${CYAN}restart${NC}               Restart Selenium Grid containers"
    echo -e "  ${CYAN}status${NC}                Display current Grid health, nodes, and available browser slots"
    echo -e "  ${CYAN}logs [service]${NC}        View live container logs (options: selenium-hub, chrome-node, firefox-node)"
    echo -e "  ${CYAN}scale <chrome|firefox> <n>${NC} Scale Chrome or Firefox nodes (e.g., ./grid.sh scale chrome 3)"
    echo -e "  ${CYAN}open-ui${NC}               Open the Selenium Grid 4 Web Console in your default browser"
    echo -e "  ${CYAN}run-tests${NC}             Run the full parallel TestNG suite (testng-grid.xml) on the Grid"
    echo -e "  ${CYAN}report${NC}                Generate and open the Allure test report in your browser"
    echo -e "  ${CYAN}help${NC}                  Show this help menu"
    echo ""
    echo -e "${BOLD}Examples:${NC}"
    echo "  ./grid.sh start"
    echo "  ./grid.sh status"
    echo "  ./grid.sh open-ui"
    echo "  ./grid.sh run-tests"
    echo "  ./grid.sh scale chrome 2"
    echo "  ./grid.sh logs chrome-node"
    echo "  ./grid.sh stop"
}

# CLI Argument Router
case "$1" in
    start|up)
        start_grid
        ;;
    stop|down)
        stop_grid
        ;;
    restart)
        restart_grid
        ;;
    status)
        status_grid
        ;;
    logs)
        logs_grid "$2"
        ;;
    scale)
        scale_grid "$2" "$3"
        ;;
    open-ui)
        open_ui
        ;;
    run-tests)
        run_tests
        ;;
    report)
        open_report
        ;;
    help|--help|-h|"")
        show_help
        ;;
    *)
        echo -e "${RED}[ERROR] Unknown command: '$1'${NC}"
        show_help
        exit 1
        ;;
esac
