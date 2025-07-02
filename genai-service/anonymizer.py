from dotenv import load_dotenv
from typing import Annotated, Literal, List
from pydantic import BaseModel,RootModel
from typing_extensions import TypedDict
from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langchain.chat_models import init_chat_model

load_dotenv()

llm = init_chat_model("openai:gpt-4.1-mini")

class ChangedTerm(TypedDict):
    original: str
    anonymized: str

class AnonymizerState(TypedDict):
    messages: Annotated[list, add_messages]
    level: Literal["light", "medium", "high"]
    changed_terms: list[ChangedTerm] | None

class ChangedTermsResponse(BaseModel):  
    changed_terms: List[ChangedTerm]


def extract_terms(state: AnonymizerState):
    structured_llm = llm.with_structured_output(ChangedTermsResponse)
    user_msg = state["messages"][-1]
    level = state["level"]

    level_prompt_map = {
        "light": "Extract a list of names to anonymize. Replace each with a generic label like 'Person A'.",
        "medium": "Extract a list of names, dates, and locations. Replace with generic labels like 'Person A', 'Date A', etc.",
        "high": "Extract names, dates, locations, genders, professions, and identifying info. Replace with 'Person A', 'Location B', etc."
    }

    system_prompt = (
        f"You are an anonymization assistant. "
        f"{level_prompt_map[level]} "
        "Respond with structured data only — a list of objects, each with `original` and `anonymized` fields."
    )

    messages = [
        {"role": "system", "content": system_prompt},
        {"role": "user", "content": user_msg.content}
    ]

    changed_terms = structured_llm.invoke(messages).changed_terms

    return {"changed_terms": changed_terms}

# Build graph with single node
graph_builder = StateGraph(AnonymizerState)

graph_builder.add_node("extract_terms", extract_terms)

graph_builder.add_edge(START, "extract_terms")
graph_builder.add_edge("extract_terms", END)

graph = graph_builder.compile()

