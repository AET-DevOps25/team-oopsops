"""Pytest configuration and shared fixtures for API testing."""

import json
import pytest
from unittest.mock import patch, MagicMock
from fastapi.testclient import TestClient
from pathlib import Path
import tempfile
import os


@pytest.fixture
def test_client():
    """Create a test client for the FastAPI app."""
    with patch.dict(os.environ, {"ANONYMIZATION_SERVICE_URL": "http://mock-service"}):
        from main import app
        return TestClient(app)


@pytest.fixture
def test_data_dir():
    """Get the test data directory path."""
    return Path(__file__).parent / "test_data"


@pytest.fixture
def mock_responses(test_data_dir):
    """Load mock responses from JSON file."""
    mock_responses_file = test_data_dir / "mock_responses.json"
    if mock_responses_file.exists():
        with open(mock_responses_file, 'r') as f:
            return json.load(f)
    return {}


@pytest.fixture
def test_scenarios(test_data_dir):
    """Load test scenarios from JSON file."""
    scenarios_file = test_data_dir / "test_scenarios.json"
    if scenarios_file.exists():
        with open(scenarios_file, 'r') as f:
            return json.load(f)
    return {"scenarios": [], "edge_cases": []}


@pytest.fixture
def sample_pdf_file(test_data_dir):
    """Get sample PDF file path."""
    return test_data_dir / "sample.pdf"


@pytest.fixture
def minimal_pdf_file(test_data_dir):
    """Get minimal PDF file path."""
    return test_data_dir / "minimal.pdf"


@pytest.fixture
def corrupted_pdf_file(test_data_dir):
    """Get corrupted PDF file path."""
    return test_data_dir / "corrupted.pdf"


@pytest.fixture
def large_pdf_file(test_data_dir):
    """Get large PDF file path."""
    return test_data_dir / "large_document.pdf"


@pytest.fixture
def sample_anonymize_request():
    """Sample anonymization request."""
    return {
        "originalText": "John Doe lives at 123 Main Street. His SSN is 123-45-6789.",
        "level": "medium"
    }


@pytest.fixture
def sample_summarize_request():
    """Sample summarization request."""
    return {
        "originalText": "This is a confidential document containing personal information including names, addresses, and social security numbers.",
        "level": "medium"
    }


@pytest.fixture
def sample_chat_request():
    """Sample chat request."""
    return {
        "messages": [
            {"role": "user", "content": "What should I anonymize in this document?"}
        ],
        "document": "John Doe lives at 123 Main Street. His SSN is 123-45-6789."
    }


@pytest.fixture
def sample_conversation_request():
    """Sample conversation request."""
    return {
        "conversation_id": "test-conversation-123",
        "query": "What personal information is in this document?",
        "document_ids": ["doc1", "doc2"]
    }


@pytest.fixture
def sample_markdown_upload_request():
    """Sample markdown upload request."""
    return {
        "content": "# Test Document\n\nThis is a test document with personal information:\n- John Doe\n- 123 Main Street\n- SSN: 123-45-6789",
        "filename": "test_document.md"
    }


@pytest.fixture
def mock_anonymization_service():
    """Mock the external anonymization service."""
    with patch('main.call_anonymization_service') as mock_service:
        mock_service.return_value = "Anonymized text with [REDACTED] information"
        yield mock_service


@pytest.fixture
def mock_llm_response():
    """Mock LLM response."""
    mock_response = MagicMock()
    mock_response.content = "Mocked LLM response"
    return mock_response


@pytest.fixture
def mock_anonymizer_graph():
    """Mock the anonymizer graph."""
    with patch('main.anonymizer_graph') as mock_graph:
        mock_graph.invoke.return_value = {
            "changed_terms": [
                {"original": "John Doe", "anonymized": "Person A"},
                {"original": "123-45-6789", "anonymized": "[SSN]"}
            ]
        }
        yield mock_graph


@pytest.fixture
def mock_summarizer_graph():
    """Mock the summarizer graph."""
    with patch('main.summarizer_graph') as mock_graph:
        mock_graph.invoke.return_value = {
            "summarized_text": "This document contains personal information that should be anonymized."
        }
        yield mock_graph


@pytest.fixture
def mock_vector_store():
    """Mock the vector store manager."""
    with patch('main.vector_store') as mock_store:
        mock_store.ingest_pdf.return_value = ("doc123", 5)
        mock_store.ingest_markdown.return_value = ("doc456", 3)
        mock_store.list_documents.return_value = [
            {"document_id": "doc1", "filename": "test.pdf", "chunks_created": 5}
        ]
        mock_store.delete_document.return_value = True
        yield mock_store


@pytest.fixture
def mock_conversation_manager():
    """Mock the conversation manager."""
    with patch('main.conversation_manager') as mock_manager:
        mock_manager.chat.return_value = {
            "response": "This document contains personal information including names and addresses.",
            "sources": [
                {"document_id": "doc1", "filename": "test.pdf", "chunk_index": 0}
            ],
            "conversation_id": "test-conversation-123"
        }
        mock_manager.get_conversation_history.return_value = [
            {"role": "user", "content": "What's in this document?"},
            {"role": "assistant", "content": "This document contains personal information."}
        ]
        mock_manager.clear_conversation.return_value = True
        yield mock_manager


@pytest.fixture(autouse=True)
def setup_test_environment():
    """Set up test environment before each test."""
    # Ensure test data directory exists
    test_data_dir = Path(__file__).parent / "test_data"
    test_data_dir.mkdir(exist_ok=True)
    
    # Generate test PDFs if they don't exist
    if not (test_data_dir / "sample.pdf").exists():
        import subprocess
        import sys
        try:
            subprocess.run([
                sys.executable, 
                str(test_data_dir / "generate_test_pdfs.py")
            ], check=True, cwd=str(test_data_dir))
        except (subprocess.CalledProcessError, FileNotFoundError):
            # If PDF generation fails, create dummy files
            for pdf_name in ["sample.pdf", "minimal.pdf", "empty.pdf", "corrupted.pdf"]:
                with open(test_data_dir / pdf_name, 'wb') as f:
                    if pdf_name == "corrupted.pdf":
                        f.write(b"This is not a valid PDF file content")
                    else:
                        f.write(b"%PDF-1.4\nDummy PDF content for testing")
