from fastapi import FastAPI, UploadFile, File, HTTPException
from fastapi.responses import JSONResponse
from models import (
    AnonymizeRequest,
    GenAiResponse,
    SummarizeRequest,
    ChatRequest,
    ChatResponse,
    DocumentUploadResponse,
    MarkdownUploadRequest,
    TextUploadRequest,
    ConversationRequest,
    ConversationResponse,
    DocumentListResponse,
)
from anonymizer import graph as anonymizer_graph
from summarizer import graph as summarizer_graph
from langchain.chat_models import init_chat_model
from vector_store import VectorStoreManager
from conversation_chain import ConversationManager
import os
import tempfile
import shutil
import requests
import os
from prometheus_fastapi_instrumentator import Instrumentator


app = FastAPI(title="GenAI Service with RAG", version="1.0.0")

# Initialize the instrumentator
instrumentator = Instrumentator(
    should_group_status_codes=False,
    should_ignore_untemplated=True,
    should_instrument_requests_inprogress=True,
    excluded_handlers=[".*admin.*", "/metrics"],
)

# Instrument the app and expose the metrics
instrumentator.instrument(app).expose(app)


# Add error handlers
@app.exception_handler(RuntimeError)
async def runtime_error_handler(request, exc):
    if "Service unavailable" in str(exc):
        return JSONResponse(status_code=503, content={"detail": str(exc)})
    return JSONResponse(
        status_code=500, content={"detail": "Internal server error"}
    )


@app.exception_handler(Exception)
async def general_exception_handler(request, exc):
    return JSONResponse(
        status_code=500, content={"detail": "Internal server error"}
    )


llm = init_chat_model("openai:gpt-4-0125-preview")
ANONYMIZATION_SERVICE_URL = os.getenv("ANONYMIZATION_SERVICE_URL")


vector_store = VectorStoreManager(persist_directory="./chroma_db")
conversation_manager = ConversationManager(vector_store)


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/api/v1/genai/anonymize", response_model=GenAiResponse)
def anonymize(request: AnonymizeRequest):
    try:
        result = anonymizer_graph.invoke(
            {
                "messages": [
                    {"role": "user", "content": request.originalText}
                ],
                "level": request.level,
                "anonymized_text": None,
            }
        )

        changed_terms = result["changed_terms"]
        anonymized_text = call_anonymization_service(
            original_text=request.originalText, changed_terms=changed_terms
        )

        return GenAiResponse(
            responseText=anonymized_text, changedTerms=changed_terms
        )

    except RuntimeError as e:
        if "Service unavailable" in str(e):
            raise HTTPException(status_code=503, detail=str(e))
        raise HTTPException(status_code=500, detail="Internal server error")
    except Exception as e:
        raise HTTPException(status_code=500, detail="Internal server error")


def call_anonymization_service(
    original_text: str, changed_terms: list[dict]
) -> str:
    payload = {"originalText": original_text, "changedTerms": changed_terms}

    try:
        response = requests.post(
            f"{ANONYMIZATION_SERVICE_URL}/replace", json=payload
        )
        response.raise_for_status()
        return response.text
    except requests.RequestException as e:
        raise RuntimeError("Service unavailable")


@app.post("/api/v1/genai/summarize", response_model=GenAiResponse)
def summarize(request: SummarizeRequest):
    result = summarizer_graph.invoke(
        {
            "messages": [{"role": "user", "content": request.originalText}],
            "level": request.level,
            "summarized_text": None,
        }
    )
    return GenAiResponse(responseText=result["summarized_text"])


@app.post("/api/v1/genai/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    messages = []
    if request.document:
        messages.append(
            {
                "role": "system",
                "content": f"You have access to the following document. Use it when answering:\n\n{request.document}",
            }
        )

    for msg in request.messages:
        messages.append({"role": msg.role, "content": msg.content})

    reply = llm.invoke(messages)
    return ChatResponse(reply=reply.content)


@app.post("/api/v1/genai/documents/upload", response_model=DocumentUploadResponse)
async def upload_document(file: UploadFile = File(...)):
    if not file.filename.endswith(".pdf"):
        raise HTTPException(
            status_code=400, detail="Only PDF files are supported"
        )

    try:
        with tempfile.NamedTemporaryFile(
            delete=False, suffix=".pdf"
        ) as tmp_file:
            shutil.copyfileobj(file.file, tmp_file)
            tmp_path = tmp_file.name

        document_id, chunks = vector_store.ingest_pdf(tmp_path, file.filename)

        os.unlink(tmp_path)

        return DocumentUploadResponse(
            document_id=document_id,
            filename=file.filename,
            chunks_created=chunks,
        )
    except Exception as e:
        if "tmp_path" in locals():
            os.unlink(tmp_path)
        raise HTTPException(status_code=500, detail=str(e))


@app.post("/api/v1/genai/documents/upload-markdown", response_model=DocumentUploadResponse)
def upload_markdown(request: MarkdownUploadRequest):
    try:
        document_id, chunks = vector_store.ingest_markdown(
            content=request.content, filename=request.filename
        )

        return DocumentUploadResponse(
            document_id=document_id,
            filename=request.filename,
            chunks_created=chunks,
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


# New endpoint for uploading text content
@app.post("/api/v1/genai/documents/upload-text", response_model=DocumentUploadResponse)
def upload_text(request: TextUploadRequest):
    try:
        document_id, chunks = vector_store.ingest_text(
            content=request.content,
            title=request.title,
            metadata=request.metadata,
        )

        return DocumentUploadResponse(
            document_id=document_id,
            filename=request.title,
            chunks_created=chunks,
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/api/v1/genai/documents", response_model=DocumentListResponse)
def list_documents():
    documents = vector_store.list_documents()
    return DocumentListResponse(documents=documents, total=len(documents))


@app.delete("/api/v1/genai/documents/{document_id}")
def delete_document(document_id: str):
    success = vector_store.delete_document(document_id)
    if not success:
        raise HTTPException(status_code=404, detail="Document not found")
    return {"status": "success", "message": f"Document {document_id} deleted"}


@app.post("/api/v1/genai/conversation/chat", response_model=ConversationResponse)
def chat_with_documents(request: ConversationRequest):
    try:
        result = conversation_manager.chat(
            conversation_id=request.conversation_id,
            query=request.query,
            document_ids=request.document_ids,
        )

        return ConversationResponse(
            response=result["response"],
            sources=result["sources"],
            conversation_id=result["conversation_id"],
        )
    except Exception as e:
        raise HTTPException(status_code=500, detail=str(e))


@app.get("/api/v1/genai/conversation/{conversation_id}/history")
def get_conversation_history(conversation_id: str):
    history = conversation_manager.get_conversation_history(conversation_id)
    return {"conversation_id": conversation_id, "history": history}


@app.delete("/api/v1/genai/conversation/{conversation_id}")
def clear_conversation(conversation_id: str):
    success = conversation_manager.clear_conversation(conversation_id)
    if not success:
        raise HTTPException(status_code=404, detail="Conversation not found")
    return {
        "status": "success",
        "message": f"Conversation {conversation_id} cleared",
    }
