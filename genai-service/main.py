from fastapi import FastAPI
from models import AnonymizeRequest, GenAiResponse, SummarizeRequest, ChatRequest, ChatResponse, ChatMessage
from anonymizer import graph as anonymizer_graph
from summarizer import graph as summarizer_graph
from langchain.chat_models import init_chat_model

app = FastAPI()

llm = init_chat_model("openai:gpt-4-0125-preview")

@app.get("/health")
def health_check():
    return {"status": "ok"}


@app.post("/anonymize", response_model=GenAiResponse)
def anonymize(request: AnonymizeRequest):
    result = anonymizer_graph.invoke({
        "messages": [{"role": "user", "content": request.originalText}],
        "level": request.level,
        "anonymized_text": None
    })

    return GenAiResponse(reponseText=result["anonymized_text"])

@app.post("/summarize", response_model=GenAiResponse)
def summarize(request: SummarizeRequest):
    result = summarizer_graph.invoke({
        "messages": [{"role": "user", "content": request.originalText}],
        "level": request.level,
        "summarized_text": None
    })
    return GenAiResponse(reponseText=result["summarized_text"])

@app.post("/chat", response_model=ChatResponse)
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
