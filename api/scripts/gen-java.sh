#!/bin/bash

# Generate only Java server stubs and client libraries

set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
source "${SCRIPT_DIR}/gen-all.sh"

echo -e "${GREEN}📦 Generating Java code only...${NC}"

for service in document-service authentication-service anonymization-service; do
    service_dir=$(echo "$service" | sed 's/-service$//')
    generate_java_server "$service_dir"
    generate_java_client "$service_dir"
done

echo -e "${GREEN}✅ Java code generation completed!${NC}"