#!/usr/bin/env bash
set -euo pipefail

# ------------------------------------------------------------------
# Florestou — Production startup script
# ------------------------------------------------------------------

SCRIPT_DIR="$(cd "$(dirname "$0")" && pwd)"
PROJECT_DIR="$(cd "$SCRIPT_DIR/.." && pwd)"

# --- Required environment variables ---

REQUIRED_VARS=(DB_HOST DB_PORT DB_NAME DB_USER DB_PASSWORD)
MISSING=()

for var in "${REQUIRED_VARS[@]}"; do
  if [[ -z "${!var:-}" ]]; then
    MISSING+=("$var")
  fi
done

if [[ ${#MISSING[@]} -gt 0 ]]; then
  echo "ERROR: Missing required environment variables:"
  printf '  %s\n' "${MISSING[@]}"
  echo ""
  echo "Usage:"
  echo "  DB_HOST=.. DB_PORT=.. DB_NAME=.. DB_USER=.. DB_PASSWORD=.. $0"
  echo ""
  echo "Optional:"
  echo "  HTTP_PORT     (default: 3000)"
  echo "  JVM_OPTS      (default: -Xms256m -Xmx512m)"
  exit 1
fi

# --- Defaults ---

export HTTP_PORT="${HTTP_PORT:-3000}"
JVM_OPTS="${JVM_OPTS:--Xms256m -Xmx512m}"

# --- Pre-flight checks ---

echo "--- Florestou pre-flight checks ---"

# Java
if ! command -v java &>/dev/null; then
  echo "ERROR: java not found in PATH"
  exit 1
fi
JAVA_VERSION=$(java -version 2>&1 | head -1)
echo "[ok] Java: $JAVA_VERSION"

# Clojure CLI
if ! command -v clj &>/dev/null; then
  echo "ERROR: clj not found in PATH"
  exit 1
fi
echo "[ok] Clojure CLI available"

# Database connectivity
echo -n "[..] Database $DB_HOST:$DB_PORT/$DB_NAME ... "
if command -v pg_isready &>/dev/null; then
  if pg_isready -h "$DB_HOST" -p "$DB_PORT" -d "$DB_NAME" -U "$DB_USER" -q 2>/dev/null; then
    echo "reachable"
  else
    echo "UNREACHABLE (pg_isready failed)"
    echo "WARNING: Database may not be ready. Proceeding anyway."
  fi
elif command -v nc &>/dev/null; then
  if nc -z -w3 "$DB_HOST" "$DB_PORT" 2>/dev/null; then
    echo "port open"
  else
    echo "UNREACHABLE (port $DB_PORT closed)"
    exit 1
  fi
else
  echo "skipped (no pg_isready or nc)"
fi

# --- Start ---

echo ""
echo "--- Starting Florestou ---"
echo "  HTTP_PORT=$HTTP_PORT"
echo "  DB_HOST=$DB_HOST:$DB_PORT/$DB_NAME"
echo "  JVM_OPTS=$JVM_OPTS"
echo ""

cd "$PROJECT_DIR"

# shellcheck disable=SC2086
exec clj $JVM_OPTS -M -m florestou.core
