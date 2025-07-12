#!/bin/bash

# Test runner script for genai-service
set -e

echo "Running tests..."

# Run tests with coverage
python -m pytest tests/ --cov=main --cov-report=html --cov-report=term-missing -v --tb=short

echo "Test run complete!"
echo "Coverage report available in htmlcov/index.html"