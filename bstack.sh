#!/usr/bin/env bash

# ==============================================================================
# BrowserStack Automate Cloud Execution CLI
# E-commerce Checkout Process Automation Framework
# ==============================================================================

set -e

# Color definitions
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
BOLD='\033[1m'
NC='\033[0m' # No Color

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
cd "$SCRIPT_DIR"

CONFIG_FILE="$SCRIPT_DIR/src/test/resources/config/config.properties"

# Print banner
print_banner() {
    echo -e "${CYAN}${BOLD}"
    echo "=========================================================="
    echo "     BROWSERSTACK CLOUD TEST RUNNER UTILITY              "
    echo "=========================================================="
    echo -e "${NC}"
}

# Helper to read property from config.properties
get_property() {
    local key="$1"
    if [ -f "$CONFIG_FILE" ]; then
        grep -v '^[[:space:]]*#' "$CONFIG_FILE" | grep "^${key}=" | head -n 1 | cut -d'=' -f2- | tr -d '\r' | xargs
    fi
}

# Check prerequisites
check_prerequisites() {
    if ! command -v mvn &> /dev/null; then
        echo -e "${RED}[ERROR] Apache Maven ('mvn') is not installed or not in your PATH.${NC}"
        exit 1
    fi

    if [ ! -f "$CONFIG_FILE" ]; then
        echo -e "${RED}[ERROR] Configuration file not found at: $CONFIG_FILE${NC}"
        exit 1
    fi
}

# Verify BrowserStack credentials & plan status
check_status() {
    print_banner
    check_prerequisites

    local username=$(get_property "browserstack_username")
    local accesskey=$(get_property "browserstack_accesskey")
    local hub_url=$(get_property "browserstack_url")

    echo -e "${BLUE}[INFO] Checking BrowserStack configuration...${NC}"
    echo -e "  Hub URL  : ${CYAN}${hub_url:-Not Set}${NC}"
    echo -e "  Username : ${CYAN}${username:-Not Set}${NC}"
    echo -e "  Key      : ${CYAN}***${accesskey: -4}${NC}"

    if [ -z "$username" ] || [ "$username" == "YOUR_BROWSERSTACK_USERNAME" ]; then
        echo -e "\n${RED}[ERROR] BrowserStack username is not configured in config.properties.${NC}"
        echo -e "Please set 'browserstack_username' in: ${BOLD}$CONFIG_FILE${NC}"
        exit 1
    fi

    if [ -z "$accesskey" ] || [ "$accesskey" == "YOUR_BROWSERSTACK_ACCESSKEY" ]; then
        echo -e "\n${RED}[ERROR] BrowserStack access key is not configured in config.properties.${NC}"
        echo -e "Please set 'browserstack_accesskey' in: ${BOLD}$CONFIG_FILE${NC}"
        exit 1
    fi

    echo -e "\n${YELLOW}[INFO] Verifying credentials with BrowserStack Automate API...${NC}"
    
    local response
    response=$(curl -s -u "${username}:${accesskey}" "https://api.browserstack.com/automate/plan.json" 2>&1) || true

    if echo "$response" | grep -q '"automate_plan"'; then
        echo -e "${GREEN}${BOLD}[SUCCESS] BrowserStack credentials verified! Connected to Automate cloud.${NC}"
        echo -e "Plan Info : ${GREEN}${response}${NC}"
    elif echo "$response" | grep -q '"parallel_sessions_running"'; then
        echo -e "${GREEN}${BOLD}[SUCCESS] BrowserStack credentials verified! Connected to Automate cloud.${NC}"
        echo -e "Plan Info : ${GREEN}${response}${NC}"
    else
        echo -e "${RED}[ERROR] Failed to authenticate with BrowserStack.${NC}"
        echo -e "Response  : $response"
        exit 1
    fi
}

# Run a single test class or method
run_single_test() {
    local test_name="$1"
    local browser="${2:-chrome}"

    if [ -z "$test_name" ]; then
        echo -e "${RED}[ERROR] Please provide a test class name (e.g., ContactUsTest, CartTest).${NC}"
        echo "Usage: $0 test <TestClassName> [browser: chrome|firefox|edge]"
        exit 1
    fi

    print_banner
    check_prerequisites

    echo -e "${BLUE}[INFO] Running single test: ${BOLD}${test_name}${NC}${BLUE} on BrowserStack using ${BOLD}${browser}${NC}..."
    mvn test -DsuiteXmlFile="" -Dtest="${test_name}" -Drunmode=browserstack -Dbrowser="${browser}"
    echo -e "\n${GREEN}[SUCCESS] Test execution finished! Run '$0 report' to view the Allure report.${NC}"
}

# Run a TestNG suite XML file
run_suite() {
    local suite_xml="${1:-src/test/resources/testng.xml}"
    local browser="${2:-chrome}"

    if [ ! -f "$suite_xml" ]; then
        echo -e "${RED}[ERROR] TestNG suite file not found: $suite_xml${NC}"
        exit 1
    fi

    print_banner
    check_prerequisites

    echo -e "${BLUE}[INFO] Running suite: ${BOLD}${suite_xml}${NC}${BLUE} on BrowserStack using ${BOLD}${browser}${NC}..."
    mvn test -DsuiteFile="${suite_xml}" -Drunmode=browserstack -Dbrowser="${browser}"
    echo -e "\n${GREEN}[SUCCESS] Suite execution finished! Run '$0 report' to view the Allure report.${NC}"
}

# Run the dedicated cloud cross-browser suite (testng-cloud.xml)
run_cloud_suite() {
    print_banner
    check_prerequisites

    echo -e "${BLUE}[INFO] Running Cloud Cross-Browser Suite: ${BOLD}src/test/resources/testng-cloud.xml${NC}..."
    mvn test -DsuiteFile="src/test/resources/testng-cloud.xml" -Drunmode=browserstack
    echo -e "\n${GREEN}[SUCCESS] Cloud suite execution finished! Run '$0 report' to view the Allure report.${NC}"
}

# Open BrowserStack Dashboard in default browser
open_dashboard() {
    local dashboard_url="https://automate.browserstack.com/dashboard/v2"

    echo -e "${BLUE}[INFO] Opening BrowserStack Automate Dashboard in browser: ${CYAN}${dashboard_url}${NC}"
    if command -v open &> /dev/null; then
        open "$dashboard_url"
    elif command -v xdg-open &> /dev/null; then
        xdg-open "$dashboard_url"
    else
        echo "Please open: $dashboard_url"
    fi
}

# Generate and open Allure report
open_report() {
    print_banner
    check_prerequisites

    if [ ! -d "target/allure-results" ] || [ -z "$(ls -A target/allure-results 2>/dev/null)" ]; then
        echo -e "${YELLOW}[WARN] No Allure results found in target/allure-results. Please run a test first.${NC}"
        exit 1
    fi

    echo -e "${BLUE}[INFO] Generating and serving Allure Report...${NC}"
    mvn allure:serve
}

# Usage help
show_help() {
    print_banner
    echo -e "${BOLD}USAGE:${NC}"
    echo "  ./bstack.sh <command> [options]"
    echo ""
    echo -e "${BOLD}COMMANDS:${NC}"
    echo "  test <TestName> [browser]   Run a specific test class (e.g., ./bstack.sh test ContactUsTest firefox)"
    echo "  suite [suite.xml] [browser] Run a TestNG XML suite (e.g., ./bstack.sh suite src/test/resources/testng.xml)"
    echo "  cloud                       Run dedicated cross-browser cloud suite (testng-cloud.xml)"
    echo "  all [browser]               Run full test suite on BrowserStack (default: chrome)"
    echo "  check                       Verify BrowserStack credentials & plan connectivity"
    echo "  open                        Open BrowserStack Automate live dashboard in your browser"
    echo "  report                      Generate and serve the Allure HTML report with session links"
    echo "  help                        Show this help message"
    echo ""
    echo -e "${BOLD}EXAMPLES:${NC}"
    echo "  ./bstack.sh check"
    echo "  ./bstack.sh test ContactUsTest"
    echo "  ./bstack.sh test CartTest firefox"
    echo "  ./bstack.sh cloud"
    echo "  ./bstack.sh open"
    echo "  ./bstack.sh report"
    echo ""
}

# Main command dispatcher
COMMAND="${1:-help}"
shift || true

case "$COMMAND" in
    test)
        run_single_test "$1" "$2"
        ;;
    suite)
        run_suite "$1" "$2"
        ;;
    cloud)
        run_cloud_suite
        ;;
    all)
        run_suite "src/test/resources/testng.xml" "${1:-chrome}"
        ;;
    check|status)
        check_status
        ;;
    open|dashboard)
        open_dashboard
        ;;
    report)
        open_report
        ;;
    help|--help|-h)
        show_help
        ;;
    *)
        echo -e "${RED}[ERROR] Unknown command: $COMMAND${NC}\n"
        show_help
        exit 1
        ;;
esac
