#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
RESULT_DIR="${ROOT_DIR}/results/order-contention"

mkdir -p "${RESULT_DIR}"
rm -rf "${RESULT_DIR}/html"

jmeter -n \
  -t "${ROOT_DIR}/jmeter/order-contention.jmx" \
  -l "${RESULT_DIR}/result.jtl" \
  -e -o "${RESULT_DIR}/html" \
  -Jscheme="${SCHEME:-http}" \
  -Jhost="${HOST:-localhost}" \
  -Jport="${PORT:-8080}" \
  -JcustomerEmail="${CUSTOMER_EMAIL:-user@example.com}" \
  -JcustomerPassword="${CUSTOMER_PASSWORD:-1234}" \
  -JproductId="${PRODUCT_ID:-1}" \
  -JorderQuantity="${ORDER_QUANTITY:-1}" \
  -Jthreads="${THREADS:-150}" \
  -JrampUp="${RAMP_UP:-30}" \
  -Jduration="${DURATION:-180}"
