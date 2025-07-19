"""Comprehensive integration tests for FastAPI endpoints."""

import pytest
import json
import io
from unittest.mock import patch


class TestHealthEndpoint:
    """Test health check endpoint."""

    @pytest.mark.integration
    def test_health_check(self, test_client):
        """Test health check endpoint."""
        response = test_client.get("/health")
        assert response.status_code == 200
        assert response.json()["status"] == "ok"


class TestAnonymizationEndpoint:
    """Test anonymization functionality via API."""

    @pytest.mark.integration
    def test_anonymize_success(
        self,
        test_client,
        sample_anonymize_request,
        mock_anonymizer_graph,
        mock_anonymization_service,
    ):
        """Test successful anonymization."""
        response = test_client.post(
            "/api/v1/genai/anonymize", json=sample_anonymize_request
        )

        assert response.status_code == 200
        result = response.json()
        assert "responseText" in result
        assert "Anonymized text" in result["responseText"]

        # Verify the graph was called
        mock_anonymizer_graph.invoke.assert_called_once()

        # Verify the anonymization service was called
        mock_anonymization_service.assert_called_once()

    @pytest.mark.integration
    @pytest.mark.parametrize("level", ["light", "medium", "high"])
    def test_anonymize_all_levels(
        self,
        test_client,
        mock_anonymizer_graph,
        mock_anonymization_service,
        level,
    ):
        """Test anonymization with different levels."""
        request_data = {
            "originalText": "John Doe lives at 123 Main Street. His SSN is 123-45-6789.",
            "level": level,
        }

        response = test_client.post(
            "/api/v1/genai/anonymize", json=request_data
        )

        assert response.status_code == 200
        result = response.json()
        assert "responseText" in result

        # Verify the graph was called with correct level
        args, kwargs = mock_anonymizer_graph.invoke.call_args
        assert args[0]["level"] == level

    @pytest.mark.integration
    def test_anonymize_with_test_scenarios(
        self,
        test_client,
        test_scenarios,
        mock_anonymizer_graph,
        mock_anonymization_service,
    ):
        """Test anonymization with different scenarios from test data."""
        for scenario in test_scenarios["scenarios"]:
            request_data = {
                "originalText": scenario["sample_text"],
                "level": scenario["expected_level"],
            }

            response = test_client.post(
                "/api/v1/genai/anonymize", json=request_data
            )

            assert response.status_code == 200
            result = response.json()
            assert "responseText" in result

    @pytest.mark.integration
    def test_anonymize_edge_cases(
        self,
        test_client,
        test_scenarios,
        mock_anonymizer_graph,
        mock_anonymization_service,
    ):
        """Test anonymization with edge cases."""
        for edge_case in test_scenarios["edge_cases"]:
            request_data = {
                "originalText": edge_case["text"],
                "level": "medium",
            }

            response = test_client.post(
                "/api/v1/genai/anonymize", json=request_data
            )

            if edge_case["name"] == "empty_document":
                # Should handle empty text gracefully
                assert response.status_code in [200, 400]
            else:
                assert response.status_code == 200

    @pytest.mark.integration
    def test_anonymize_invalid_level(self, test_client):
        """Test anonymization with invalid level."""
        request_data = {"originalText": "Some text", "level": "invalid_level"}

        response = test_client.post(
            "/api/v1/genai/anonymize", json=request_data
        )
        assert response.status_code == 422

    @pytest.mark.integration
    def test_anonymize_missing_fields(self, test_client):
        """Test anonymization with missing required fields."""
        request_data = {"level": "medium"}  # Missing originalText

        response = test_client.post(
            "/api/v1/genai/anonymize", json=request_data
        )
        assert response.status_code == 422

    @pytest.mark.integration
    def test_anonymize_service_failure(
        self, test_client, sample_anonymize_request, mock_anonymizer_graph
    ):
        """Test handling of anonymization service failure."""
        with patch("main.call_anonymization_service") as mock_service:
            mock_service.side_effect = RuntimeError("Service unavailable")

            response = test_client.post(
                "/api/v1/genai/anonymize", json=sample_anonymize_request
            )
            assert response.status_code == 503
            assert "Service unavailable" in response.json()["detail"]


class TestSummarizationEndpoint:
    """Test summarization functionality via API."""

    @pytest.mark.integration
    def test_summarize_success(
        self, test_client, sample_summarize_request, mock_summarizer_graph
    ):
        """Test successful summarization."""
        response = test_client.post(
            "/api/v1/genai/summarize", json=sample_summarize_request
        )

        assert response.status_code == 200
        result = response.json()
        assert "responseText" in result
        assert "personal information" in result["responseText"]

        # Verify the graph was called
        mock_summarizer_graph.invoke.assert_called_once()

    @pytest.mark.integration
    @pytest.mark.parametrize("level", ["short", "medium", "long"])
    def test_summarize_all_levels(
        self, test_client, mock_summarizer_graph, level
    ):
        """Test summarization with different levels."""
        request_data = {
            "originalText": "This is a long document that needs to be summarized.",
            "level": level,
        }

        response = test_client.post("/api/v1/genai/summarize", json=request_data)

        assert response.status_code == 200
        result = response.json()
        assert "responseText" in result

        # Verify the graph was called with correct level
        args, kwargs = mock_summarizer_graph.invoke.call_args
        assert args[0]["level"] == level

    @pytest.mark.integration
    def test_summarize_invalid_level(self, test_client):
        """Test summarization with invalid level."""
        request_data = {"originalText": "Some text", "level": "invalid_level"}

        response = test_client.post("/api/v1/genai/summarize", json=request_data)
        assert response.status_code == 422


class TestChatEndpoint:
    """Test chat functionality via API."""

    @pytest.mark.integration
    def test_chat_success(self, test_client, sample_chat_request):
        """Test successful chat."""
        with patch("main.llm") as mock_llm:
            mock_response = type(
                "obj",
                (object,),
                {"content": "This document contains personal information."},
            )
            mock_llm.invoke.return_value = mock_response

            response = test_client.post("/api/v1/genai/chat", json=sample_chat_request)

            assert response.status_code == 200
            result = response.json()
            assert "reply" in result
            assert "personal information" in result["reply"]

    @pytest.mark.integration
    def test_chat_with_document_context(self, test_client):
        """Test chat with document context."""
        request_data = {
            "messages": [
                {
                    "role": "user",
                    "content": "What personal information is in this document?",
                }
            ],
            "document": "John Doe, SSN: 123-45-6789, lives at 123 Main Street.",
        }

        with patch("main.llm") as mock_llm:
            mock_response = type(
                "obj",
                (object,),
                {"content": "The document contains name, SSN, and address."},
            )
            mock_llm.invoke.return_value = mock_response

            response = test_client.post("/api/v1/genai/chat", json=request_data)

            assert response.status_code == 200
            result = response.json()
            assert "reply" in result

    @pytest.mark.integration
    def test_chat_conversation_history(self, test_client):
        """Test chat with conversation history."""
        request_data = {
            "messages": [
                {"role": "user", "content": "What's in this document?"},
                {
                    "role": "assistant",
                    "content": "This document contains personal information.",
                },
                {"role": "user", "content": "Should I anonymize it?"},
            ]
        }

        with patch("main.llm") as mock_llm:
            mock_response = type(
                "obj",
                (object,),
                {
                    "content": "Yes, you should anonymize the personal information."
                },
            )
            mock_llm.invoke.return_value = mock_response

            response = test_client.post("/api/v1/genai/chat", json=request_data)

            assert response.status_code == 200
            result = response.json()
            assert "reply" in result


class TestDocumentManagementEndpoints:
    """Test document upload and management via API."""

    @pytest.mark.integration
    def test_upload_pdf_success(
        self, test_client, sample_pdf_file, mock_vector_store
    ):
        """Test successful PDF upload."""
        if not sample_pdf_file.exists():
            pytest.skip("Sample PDF file not found")

        with open(sample_pdf_file, "rb") as f:
            files = {"file": ("test.pdf", f, "application/pdf")}
            response = test_client.post("/documents/upload", files=files)

        assert response.status_code == 200
        result = response.json()
        assert "document_id" in result
        assert "filename" in result
        assert "chunks_created" in result

        # Verify vector store was called
        mock_vector_store.ingest_pdf.assert_called_once()

    @pytest.mark.integration
    def test_upload_markdown_success(
        self, test_client, sample_markdown_upload_request, mock_vector_store
    ):
        """Test successful markdown upload."""
        response = test_client.post(
            "/documents/upload-markdown", json=sample_markdown_upload_request
        )

        assert response.status_code == 200
        result = response.json()
        assert "document_id" in result
        assert "filename" in result
        assert "chunks_created" in result

        # Verify vector store was called
        mock_vector_store.ingest_markdown.assert_called_once()

    @pytest.mark.integration
    def test_upload_non_pdf_file(self, test_client):
        """Test upload of non-PDF file."""
        files = {
            "file": ("test.txt", io.BytesIO(b"test content"), "text/plain")
        }
        response = test_client.post("/documents/upload", files=files)

        assert response.status_code == 400

    @pytest.mark.integration
    def test_list_documents(self, test_client, mock_vector_store):
        """Test listing documents."""
        response = test_client.get("/documents")

        assert response.status_code == 200
        result = response.json()
        assert "documents" in result
        assert "total" in result

        # Verify vector store was called
        mock_vector_store.list_documents.assert_called_once()

    @pytest.mark.integration
    def test_delete_document_success(self, test_client, mock_vector_store):
        """Test successful document deletion."""
        document_id = "test-doc-123"
        response = test_client.delete(f"/documents/{document_id}")

        assert response.status_code == 200
        result = response.json()
        assert result["status"] == "success"

        # Verify vector store was called
        mock_vector_store.delete_document.assert_called_once_with(document_id)

    @pytest.mark.integration
    def test_delete_nonexistent_document(self, test_client, mock_vector_store):
        """Test deletion of non-existent document."""
        mock_vector_store.delete_document.return_value = False

        document_id = "nonexistent-doc"
        response = test_client.delete(f"/documents/{document_id}")

        assert response.status_code == 404


class TestConversationEndpoints:
    """Test conversation functionality via API."""

    @pytest.mark.integration
    def test_conversation_chat_success(
        self,
        test_client,
        sample_conversation_request,
        mock_conversation_manager,
    ):
        """Test successful conversation chat."""
        response = test_client.post(
            "/conversation/chat", json=sample_conversation_request
        )

        assert response.status_code == 200
        result = response.json()
        assert "response" in result
        assert "sources" in result
        assert "conversation_id" in result

        # Verify conversation manager was called
        mock_conversation_manager.chat.assert_called_once()

    @pytest.mark.integration
    def test_get_conversation_history(
        self, test_client, mock_conversation_manager
    ):
        """Test getting conversation history."""
        conversation_id = "test-conversation-123"
        response = test_client.get(f"/conversation/{conversation_id}/history")

        assert response.status_code == 200
        result = response.json()
        assert "conversation_id" in result
        assert "history" in result

        # Verify conversation manager was called
        mock_conversation_manager.get_conversation_history.assert_called_once_with(
            conversation_id
        )

    @pytest.mark.integration
    def test_clear_conversation_success(
        self, test_client, mock_conversation_manager
    ):
        """Test successful conversation clearing."""
        conversation_id = "test-conversation-123"
        response = test_client.delete(f"/conversation/{conversation_id}")

        assert response.status_code == 200
        result = response.json()
        assert result["status"] == "success"

        # Verify conversation manager was called
        mock_conversation_manager.clear_conversation.assert_called_once_with(
            conversation_id
        )

    @pytest.mark.integration
    def test_clear_nonexistent_conversation(
        self, test_client, mock_conversation_manager
    ):
        """Test clearing non-existent conversation."""
        mock_conversation_manager.clear_conversation.return_value = False

        conversation_id = "nonexistent-conversation"
        response = test_client.delete(f"/conversation/{conversation_id}")

        assert response.status_code == 404


class TestErrorHandling:
    """Test error handling across all endpoints."""

    @pytest.mark.integration
    def test_internal_server_error_handling(
        self, test_client, sample_anonymize_request
    ):
        """Test handling of internal server errors."""
        with patch("main.anonymizer_graph") as mock_graph:
            mock_graph.invoke.side_effect = Exception("Unexpected error")

            response = test_client.post(
                "/api/v1/genai/anonymize", json=sample_anonymize_request
            )
            assert response.status_code == 500

    @pytest.mark.integration
    def test_validation_error_handling(self, test_client):
        """Test handling of validation errors."""
        invalid_request = {"invalidField": "value"}
        response = test_client.post(
            "/api/v1/genai/anonymize", json=invalid_request
        )
        assert response.status_code == 422

    @pytest.mark.integration
    def test_file_upload_error_handling(self, test_client, corrupted_pdf_file):
        """Test handling of file upload errors."""
        if not corrupted_pdf_file.exists():
            pytest.skip("Corrupted PDF file not found")

        with open(corrupted_pdf_file, "rb") as f:
            files = {"file": ("corrupted.pdf", f, "application/pdf")}
            response = test_client.post("/documents/upload", files=files)

        # Should handle corrupted files gracefully
        assert response.status_code in [400, 500]
