#!/bin/zsh

set -euo pipefail

ROOT_DIR="$(cd "$(dirname "$0")/.." && pwd)"
RESULT_DIR="${ROOT_DIR}/results/product-cache"

mkdir -p "${RESULT_DIR}"
rm -rf "${RESULT_DIR}/html"

jmeter -n \
  -t "${ROOT_DIR}/jmeter/product-cache.jmx" \
  -l "${RESULT_DIR}/result.jtl" \
  -e -o "${RESULT_DIR}/html" \
  -Jscheme="${SCHEME:-http}" \
  -Jhost="${HOST:-localhost}" \
  -Jport="${PORT:-8080}" \
  -JproductId="${PRODUCT_ID:-1}" \
  -Jthreads="${THREADS:-200}" \
  -JrampUp="${RAMP_UP:-20}" \
  -Jduration="${DURATION:-180}"
