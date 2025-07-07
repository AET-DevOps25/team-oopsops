#!/bin/bash

# Generate only TypeScript client for frontend

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/gen-all.sh"

echo -e "${GREEN}📱 Generating TypeScript code only...${NC}"
generate_typescript_client
echo -e "${GREEN}✅ TypeScript code generation completed!${NC}"