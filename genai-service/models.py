from pydantic import BaseModel
from typing import Literal, List, Optional, Dict, Any


class AnonymizeRequest(BaseModel):
    originalText: str
    level: Literal["light", "medium", "high"]


class GenAiResponse(BaseModel):
    reponseText: str
    status: str = "success"

class SummarizeRequest(BaseModel):
    originalText: str
    level: Literal["short", "medium", "long"]

class ChatMessage(BaseModel):
    role: Literal["user", "assistant"]
    content: str

class ChatRequest(BaseModel):
    messages: List[ChatMessage]
    document: Optional[str] = None

class ChatResponse(BaseModel):
    reply: str
    status: str = "success"


class DocumentUploadResponse(BaseModel):
    document_id: str
    filename: str
    chunks_created: int
    status: str = "success"


class MarkdownUploadRequest(BaseModel):
    filename: str
    content: str


class ConversationRequest(BaseModel):
    conversation_id: str
    query: str
    document_ids: Optional[List[str]] = None


class ConversationResponse(BaseModel):
    response: str
    sources: List[Dict[str, Any]]
    conversation_id: str
    status: str = "success"


class DocumentListResponse(BaseModel):
    documents: List[Dict[str, Any]]
    total: int
    status: str = "success"