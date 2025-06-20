from dotenv import load_dotenv
from typing import Annotated, Literal
from pydantic import BaseModel
from typing_extensions import TypedDict
from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langchain.chat_models import init_chat_model

load_dotenv()

llm = init_chat_model("openai:gpt-4-0125-preview")

class AnonymizerState(TypedDict):
    messages: Annotated[list, add_messages]
    level: Literal["light", "medium", "high"]
    anonymized_text: str | None

def anonymize_single_node(state: AnonymizerState):
    user_msg = state["messages"][-1]
    level = state["level"]

    level_prompt_map = {
        "light": "Lightly anonymize the text (e.g., remove names).",
        "medium": "Anonymize names, dates, and locations.",
        "high": "Aggressively anonymize names, dates, locations, genders, professions, and any identifying info."
    }

    system_prompt = f"You are an anonymizer. {level_prompt_map[level]}"

    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_msg.content}
    ]

    reply = llm.invoke(messages)
    return {"anonymized_text": reply.content}

# Build graph with single node
graph_builder = StateGraph(AnonymizerState)

graph_builder.add_node("anonymize", anonymize_single_node)

graph_builder.add_edge(START, "anonymize")
graph_builder.add_edge("anonymize", END)

graph = graph_builder.compile()

