#!/bin/bash
# ==============================================================================
# Helper script to encrypt passwords and credentials using AES-256 GCM
# Usage:
#   ./encrypt.sh "MySecretPassword"
# ==============================================================================

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
TARGET_CLASSES="${SCRIPT_DIR}/target/classes"

if [ ! -d "$TARGET_CLASSES" ]; then
    echo "[INFO] Compiling classes..."
    (cd "$SCRIPT_DIR" && mvn compile -q)
fi

SECRET="${1:-Password@123}"

java -cp "$TARGET_CLASSES" com.ecommerce.utils.EncryptionUtils "$SECRET"
