from dotenv import load_dotenv
from typing import Annotated, Literal
from pydantic import BaseModel
from typing_extensions import TypedDict
from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langchain.chat_models import init_chat_model

load_dotenv()

llm = init_chat_model("openai:gpt-4-0125-preview")

class SummarizerState(TypedDict):
    messages: Annotated[list, add_messages]
    level: Literal["short", "medium", "long"]
    summarized_text: str | None

def summarize(state: SummarizerState):
    user_msg = state["messages"][-1]
    level = state["level"]

    level_prompt_map = {
        "short": "Provide a short summary of the following text",
        "medium": "Provide a medium-length summary of the text",
        "long": "Provide a detailed summary covering all major points in multiple paragraphs."
    }

    system_prompt = f"You are a summarization assistant. {level_prompt_map[level]}"

    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_msg.content}
    ]

    reply = llm.invoke(messages)
    return {"summarized_text": reply.content}

# Build graph with single node
graph_builder = StateGraph(SummarizerState)

graph_builder.add_node("summarize", summarize)

graph_builder.add_edge(START, "summarize")
graph_builder.add_edge("summarize", END)

graph = graph_builder.compile()

