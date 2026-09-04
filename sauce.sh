#!/usr/bin/env bash

# ==============================================================================
# Sauce Labs Cloud Execution CLI
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
    echo "       SAUCE LABS CLOUD TEST RUNNER UTILITY              "
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

# Verify Sauce Labs credentials & endpoint status
check_status() {
    print_banner
    check_prerequisites

    local username=$(get_property "saucelabs_username")
    local accesskey=$(get_property "saucelabs_accesskey")
    local hub_url=$(get_property "saucelabs_url")

    echo -e "${BLUE}[INFO] Checking Sauce Labs configuration...${NC}"
    echo -e "  Endpoint : ${CYAN}${hub_url:-Not Set}${NC}"
    echo -e "  Username : ${CYAN}${username:-Not Set}${NC}"
    echo -e "  Key      : ${CYAN}***${accesskey: -4}${NC}"

    if [ -z "$username" ] || [ "$username" == "YOUR_SAUCELABS_USERNAME" ]; then
        echo -e "${RED}[ERROR] Sauce Labs username is not configured in config.properties.${NC}"
        exit 1
    fi

    if [ -z "$accesskey" ] || [ "$accesskey" == "YOUR_SAUCELABS_ACCESSKEY" ]; then
        echo -e "${RED}[ERROR] Sauce Labs access key is not configured in config.properties.${NC}"
        exit 1
    fi

    local status_endpoint="${hub_url%/}/status"
    # Convert app. to ondemand. if present
    status_endpoint="${status_endpoint/app./ondemand.}"

    echo -e "\n${YELLOW}[INFO] Pinging Sauce Labs hub status (${status_endpoint})...${NC}"
    
    local response
    response=$(curl -s -u "${username}:${accesskey}" "${status_endpoint}" 2>&1) || true

    if echo "$response" | grep -q '"ready":true'; then
        echo -e "${GREEN}${BOLD}[SUCCESS] Sauce Labs Cloud connection is verified and operational!${NC}"
        echo -e "Response : ${GREEN}${response}${NC}"
    else
        echo -e "${RED}[ERROR] Failed to verify Sauce Labs connection.${NC}"
        echo -e "Response : $response"
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

    echo -e "${BLUE}[INFO] Running single test: ${BOLD}${test_name}${NC}${BLUE} on Sauce Labs using ${BOLD}${browser}${NC}..."
    mvn test -DsuiteXmlFile="" -Dtest="${test_name}" -Drunmode=saucelabs -Dbrowser="${browser}"
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

    echo -e "${BLUE}[INFO] Running suite: ${BOLD}${suite_xml}${NC}${BLUE} on Sauce Labs using ${BOLD}${browser}${NC}..."
    mvn test -DsuiteFile="${suite_xml}" -Drunmode=saucelabs -Dbrowser="${browser}"
    echo -e "\n${GREEN}[SUCCESS] Suite execution finished! Run '$0 report' to view the Allure report.${NC}"
}

# Open Sauce Labs Dashboard in default browser
open_dashboard() {
    local hub_url=$(get_property "saucelabs_url")
    local dashboard_url="https://app.saucelabs.com"
    if [[ "$hub_url" == *"eu-central-1"* ]]; then
        dashboard_url="https://app.eu-central-1.saucelabs.com/dashboard/builds"
    else
        dashboard_url="https://app.saucelabs.com/dashboard/builds"
    fi

    echo -e "${BLUE}[INFO] Opening Sauce Labs Dashboard in browser: ${CYAN}${dashboard_url}${NC}"
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
    echo "  ./sauce.sh <command> [options]"
    echo ""
    echo -e "${BOLD}COMMANDS:${NC}"
    echo "  test <TestName> [browser]   Run a specific test class (e.g., ./sauce.sh test ContactUsTest firefox)"
    echo "  suite [suite.xml] [browser] Run a TestNG XML suite (e.g., ./sauce.sh suite src/test/resources/testng.xml)"
    echo "  all [browser]               Run full test suite (default: chrome)"
    echo "  check                       Verify Sauce Labs credentials and connection status"
    echo "  open                        Open the Sauce Labs live dashboard in your web browser"
    echo "  report                      Generate and serve the Allure HTML report with Sauce links"
    echo "  help                        Show this help message"
    echo ""
    echo -e "${BOLD}EXAMPLES:${NC}"
    echo "  ./sauce.sh check"
    echo "  ./sauce.sh test ContactUsTest"
    echo "  ./sauce.sh test CartTest firefox"
    echo "  ./sauce.sh test CheckoutTest edge"
    echo "  ./sauce.sh all chrome"
    echo "  ./sauce.sh open"
    echo "  ./sauce.sh report"
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
