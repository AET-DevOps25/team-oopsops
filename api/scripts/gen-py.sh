#!/bin/bash

# Generate only Python FastAPI server stubs

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/gen-all.sh"

echo -e "${GREEN}🐍 Generating Python code only...${NC}"
generate_python_server
echo -e "${GREEN}✅ Python code generation completed!${NC}"