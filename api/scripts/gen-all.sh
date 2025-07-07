#!/bin/bash

# Generate all client stubs and server interfaces from OpenAPI spec
# This script generates:
# - Java Spring Boot server stubs for microservices
# - Java client libraries for inter-service communication
# - Python FastAPI server stubs for GenAI service
# - TypeScript client for frontend

set -euo pipefail

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# Script directory
SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "${SCRIPT_DIR}/../.." && pwd)"
API_SPEC="${PROJECT_ROOT}/api/openapi.yaml"

echo -e "${GREEN}🚀 Starting code generation from OpenAPI spec${NC}"
echo -e "${YELLOW}Project root: ${PROJECT_ROOT}${NC}"
echo -e "${YELLOW}API spec: ${API_SPEC}${NC}"

# Navigate to project root
cd "${PROJECT_ROOT}"

# Check if OpenAPI spec exists
if [[ ! -f "${API_SPEC}" ]]; then
    echo -e "${RED}❌ OpenAPI spec not found at ${API_SPEC}${NC}"
    exit 1
fi

# Function to check and use local or global npm packages
get_command() {
    local package_name=$1
    local command_name=$2
    
    # Check if local npm package exists
    if [[ -f "node_modules/.bin/${command_name}" ]]; then
        echo "node_modules/.bin/${command_name}"
    elif command -v "${command_name}" &> /dev/null; then
        echo "${command_name}"
    else
        echo -e "${YELLOW}⚠️  ${command_name} not found. Skipping related operations.${NC}"
        return 1
    fi
}

# Get command paths
SPECTRAL_CMD=""
OPENAPI_GEN_CMD=""
REDOCLY_CMD=""

if SPECTRAL_CMD=$(get_command "@stoplight/spectral-cli" "spectral"); then
    echo -e "${GREEN}✅ Found Spectral at: ${SPECTRAL_CMD}${NC}"
else
    echo -e "${YELLOW}⚠️  Spectral not available${NC}"
fi

if OPENAPI_GEN_CMD=$(get_command "@openapitools/openapi-generator-cli" "openapi-generator-cli"); then
    echo -e "${GREEN}✅ Found OpenAPI Generator at: ${OPENAPI_GEN_CMD}${NC}"
else
    echo -e "${RED}❌ OpenAPI Generator is required but not found${NC}"
    exit 1
fi

if REDOCLY_CMD=$(get_command "@redocly/cli" "redocly"); then
    echo -e "${GREEN}✅ Found Redocly at: ${REDOCLY_CMD}${NC}"
else
    echo -e "${YELLOW}⚠️  Redocly not available${NC}"
fi

# Validate OpenAPI spec first
if [[ -n "${SPECTRAL_CMD}" ]]; then
    echo -e "${GREEN}🔍 Validating OpenAPI specification...${NC}"
    if ${SPECTRAL_CMD} lint "${API_SPEC}" --format pretty; then
        echo -e "${GREEN}✅ OpenAPI spec validation passed${NC}"
    else
        echo -e "${YELLOW}⚠️  OpenAPI spec validation failed. Continuing with code generation...${NC}"
        echo -e "${YELLOW}💡 You may want to fix the validation issues for better code generation${NC}"
    fi
else
    echo -e "${YELLOW}⚠️  Skipping OpenAPI spec validation (Spectral not available)${NC}"
fi

# Basic OpenAPI validation using openapi-generator
echo -e "${GREEN}🔍 Basic OpenAPI validation using generator...${NC}"
if ${OPENAPI_GEN_CMD} validate -i "${API_SPEC}"; then
    echo -e "${GREEN}✅ Basic OpenAPI validation passed${NC}"
else
    echo -e "${RED}❌ Basic OpenAPI validation failed. Please fix the spec before continuing.${NC}"
    exit 1
fi

# Function to generate Java Spring Boot server stubs
generate_java_server() {
    local service_name=$1
    local output_dir="${PROJECT_ROOT}/server/${service_name}/generated"
    
    echo -e "${GREEN}🏗️  Generating Java Spring Boot server stubs for ${service_name}...${NC}"
    
    # Clean existing generated code
    rm -rf "${output_dir}"
    mkdir -p "${output_dir}"
    
    if ${OPENAPI_GEN_CMD} generate \
        -i "${API_SPEC}" \
        -g spring \
        -o "${output_dir}" \
        --additional-properties=\
useTags=true,\
interfaceOnly=true,\
skipDefaultInterface=true,\
java8=false,\
dateLibrary=java8,\
serializationLibrary=jackson,\
hideGenerationTimestamp=true,\
groupId=oopsops.app.${service_name},\
artifactId=${service_name}-api,\
apiPackage=oopsops.app.${service_name}.api,\
modelPackage=oopsops.app.${service_name}.model,\
configPackage=oopsops.app.${service_name}.config; then
        echo -e "${GREEN}✅ Generated Java server stubs for ${service_name}${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to generate Java server stubs for ${service_name}${NC}"
        return 1
    fi
}

# Function to generate Java client libraries
generate_java_client() {
    local service_name=$1
    local output_dir="${PROJECT_ROOT}/server/${service_name}/generated-client"
    
    echo -e "${GREEN}🏗️  Generating Java client library for ${service_name}...${NC}"
    
    # Clean existing generated code
    rm -rf "${output_dir}"
    mkdir -p "${output_dir}"
    
    if ${OPENAPI_GEN_CMD} generate \
        -i "${API_SPEC}" \
        -g java \
        -o "${output_dir}" \
        --additional-properties=\
hideGenerationTimestamp=true,\
groupId=oopsops.app.client,\
artifactId=${service_name}-client,\
apiPackage=oopsops.app.client.${service_name}.api,\
modelPackage=oopsops.app.client.${service_name}.model,\
invokerPackage=oopsops.app.client.${service_name},\
library=okhttp-gson,\
dateLibrary=java8,\
java8=false; then
        echo -e "${GREEN}✅ Generated Java client library for ${service_name}${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to generate Java client library for ${service_name}${NC}"
        return 1
    fi
}

# Function to generate Python FastAPI server stubs
generate_python_server() {
    local output_dir="${PROJECT_ROOT}/genai-service/generated"
    
    echo -e "${GREEN}🏗️  Generating Python FastAPI server stubs...${NC}"
    
    # Clean existing generated code
    rm -rf "${output_dir}"
    mkdir -p "${output_dir}"
    
    if ${OPENAPI_GEN_CMD} generate \
        -i "${API_SPEC}" \
        -g python-fastapi \
        -o "${output_dir}" \
        --additional-properties=\
hideGenerationTimestamp=true,\
packageName=redacta_api,\
projectName=redacta-genai-api,\
packageVersion=1.0.0; then
        echo -e "${GREEN}✅ Generated Python FastAPI server stubs${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to generate Python FastAPI server stubs${NC}"
        return 1
    fi
}

# Function to generate TypeScript client for frontend
generate_typescript_client() {
    local output_dir="${PROJECT_ROOT}/client/src/generated"
    
    echo -e "${GREEN}🏗️  Generating TypeScript client for frontend...${NC}"
    
    # Clean existing generated code
    rm -rf "${output_dir}"
    mkdir -p "${output_dir}"
    
    if ${OPENAPI_GEN_CMD} generate \
        -i "${API_SPEC}" \
        -g typescript-axios \
        -o "${output_dir}" \
        --additional-properties=\
hideGenerationTimestamp=true,\
npmName=@redacta/api-client,\
npmVersion=1.0.0,\
withInterfaces=true,\
enumNameSuffix=,\
modelPropertyNaming=camelCase; then
        echo -e "${GREEN}✅ Generated TypeScript client for frontend${NC}"
        return 0
    else
        echo -e "${RED}❌ Failed to generate TypeScript client for frontend${NC}"
        return 1
    fi
}

# Track generation results
GENERATION_RESULTS=()

# Generate code for all services
echo -e "${GREEN}📦 Generating server stubs for Java services...${NC}"
for service in document-service authentication-service anonymization-service; do
    # Extract service name without suffix for directory naming
    service_dir=$(echo "$service" | sed 's/-service$//')
    
    if generate_java_server "$service_dir"; then
        GENERATION_RESULTS+=("✅ Java server stubs for ${service_dir}")
    else
        GENERATION_RESULTS+=("❌ Java server stubs for ${service_dir}")
    fi
    
    if generate_java_client "$service_dir"; then
        GENERATION_RESULTS+=("✅ Java client library for ${service_dir}")
    else
        GENERATION_RESULTS+=("❌ Java client library for ${service_dir}")
    fi
done

echo -e "${GREEN}🐍 Generating Python FastAPI stubs...${NC}"
if generate_python_server; then
    GENERATION_RESULTS+=("✅ Python FastAPI server stubs")
else
    GENERATION_RESULTS+=("❌ Python FastAPI server stubs")
fi

echo -e "${GREEN}📱 Generating TypeScript client...${NC}"
if generate_typescript_client; then
    GENERATION_RESULTS+=("✅ TypeScript client for frontend")
else
    GENERATION_RESULTS+=("❌ TypeScript client for frontend")
fi

# Generate documentation
echo -e "${GREEN}📚 Generating API documentation...${NC}"
if [[ -n "${REDOCLY_CMD}" ]]; then
    if ${REDOCLY_CMD} build-docs "${API_SPEC}" -o "${PROJECT_ROOT}/docs/api.html"; then
        echo -e "${GREEN}✅ Generated API documentation at docs/api.html${NC}"
        GENERATION_RESULTS+=("✅ API documentation")
    else
        echo -e "${YELLOW}⚠️  Failed to generate API documentation${NC}"
        GENERATION_RESULTS+=("❌ API documentation")
    fi
else
    echo -e "${YELLOW}⚠️  Redocly not found. Skipping documentation generation.${NC}"
    GENERATION_RESULTS+=("⚠️  API documentation (Redocly not available)")
fi

# Print summary
echo -e "\n${GREEN}📊 Generation Summary:${NC}"
for result in "${GENERATION_RESULTS[@]}"; do
    echo -e "   ${result}"
done

echo -e "\n${GREEN}🎉 Code generation process completed!${NC}"
echo -e "${YELLOW}📝 Next steps:${NC}"
echo -e "   1. Review generated code in server/*/generated directories"
echo -e "   2. Update your service implementations to use generated interfaces"
echo -e "   3. Add generated client dependencies to your build files"
echo -e "   4. Update frontend to use generated TypeScript client"
echo -e "   5. Review API documentation at docs/api.html (if generated)"
echo -e "\n${YELLOW}💡 Tips:${NC}"
echo -e "   • Run 'npm run api:lint' to check OpenAPI spec quality"
echo -e "   • Use individual generation scripts for faster iterations"
echo -e "   • Check the generated README files for integration instructions"