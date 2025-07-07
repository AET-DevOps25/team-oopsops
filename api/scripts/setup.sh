#!/bin/bash

# Setup script to install dependencies and make scripts executable

set -euo pipefail

echo "🔧 Setting up OpenAPI toolchain..."


# Navigate to project root
PROJECT_ROOT="$(cd "$(dirname "${BASH_SOURCE[0]}")/../.." && pwd)"
cd "${PROJECT_ROOT}"


# Check if Node.js is installed
if ! command -v node &> /dev/null; then
    echo "❌ Node.js is not installed. Please install Node.js first."
    exit 1
fi

# Check if npm is installed
if ! command -v npm &> /dev/null; then
    echo "❌ npm is not installed. Please install npm first."
    exit 1
fi

# Make all scripts executable
chmod +x api/scripts/*.sh

# Install Node.js dependencies globally
echo "🌐 Installing CLI tools globally..."
npm install -g @openapitools/openapi-generator-cli @stoplight/spectral-cli @redocly/cli

# Install pre-commit if Python is available
if command -v python3 &> /dev/null || command -v python &> /dev/null; then
    echo "🔒 Installing pre-commit..."
    if command -v pip3 &> /dev/null; then
        pip3 install pre-commit
    elif command -v pip &> /dev/null; then
        pip install pre-commit
    else
        echo "⚠️  pip not found. Please install pre-commit manually: pip install pre-commit"
    fi
    
    # Install pre-commit hooks
    echo "🪝 Installing pre-commit hooks..."
    pre-commit install || echo "⚠️  Could not install pre-commit hooks. Run 'pre-commit install' manually after installing pre-commit."
else
    echo "⚠️  Python not found. Please install Python and pre-commit manually."
fi

echo "✅ Setup completed successfully!"
echo ""
echo "🚀 Quick start:"
echo "  1. Run 'api/scripts/gen-all.sh' to generate all code"
echo "  2. Run 'pre-commit run --all-files' to lint everything"
echo "  3. Check docs/api.html for API documentation"