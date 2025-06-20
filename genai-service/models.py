from pydantic import BaseModel
from typing import Literal, List, Optional


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