from fastapi import FastAPI
from models import AnonymizeRequest, GenAiResponse, SummarizeRequest, ChatRequest, ChatResponse, ChatMessage
from anonymizer import graph as anonymizer_graph
from summarizer import graph as summarizer_graph
from langchain.chat_models import init_chat_model
import requests
import os

app = FastAPI()

llm = init_chat_model("openai:gpt-4-0125-preview")
ANONYMIZATION_SERVICE_URL = os.getenv("ANONYMIZATION_SERVICE_URL")


@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/api/v1/genai/anonymize", response_model=GenAiResponse)
def anonymize(request: AnonymizeRequest):
    result = anonymizer_graph.invoke({
        "messages": [{"role": "user", "content": request.originalText}],
        "level": request.level,
        "anonymized_text": None
    })

    changed_terms = result["changed_terms"]  # Expect this to be a list of {"original": ..., "anonymized": ...}
    
    anonymized_text = call_anonymization_service(
        original_text=request.originalText,
        changed_terms=changed_terms
    )

    return GenAiResponse(reponseText=anonymized_text)

def call_anonymization_service(original_text: str, changed_terms: list[dict]) -> str:
    payload = {
        "originalText": original_text,
        "changedTerms": changed_terms
    }

    try:
        response = requests.post(f"{ANONYMIZATION_SERVICE_URL}/replace", json=payload)
        response.raise_for_status()
        return response.text
    except requests.RequestException as e:
        raise RuntimeError(f"Failed to call anonymization service: {e}")

@app.post("/api/v1/genai/summarize", response_model=GenAiResponse)
def summarize(request: SummarizeRequest):
    result = summarizer_graph.invoke({
        "messages": [{"role": "user", "content": request.originalText}],
        "level": request.level,
        "summarized_text": None
    })
    return GenAiResponse(reponseText=result["summarized_text"])

@app.post("/api/v1/genai/chat", response_model=ChatResponse)
def chat(request: ChatRequest):
    messages = []
    if request.document:
        messages.append({
            "role": "system",
            "content": f"You have access to the following document. Use it when answering:\n\n{request.document}"
        })

    for msg in request.messages:
        messages.append({"role": msg.role, "content": msg.content})

    reply = llm.invoke(messages)
    return ChatResponse(reply=reply.content)
