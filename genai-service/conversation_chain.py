# ABOUTME: This module implements the conversation chain with RAG capabilities using LangGraph
# ABOUTME: It manages conversation memory and retrieves relevant document context for responses

from typing import List, Dict, Optional, Annotated, TypedDict, Literal
from langgraph.graph import StateGraph, START, END
from langgraph.graph.message import add_messages
from langchain.chat_models import init_chat_model
from langchain.schema import HumanMessage, AIMessage, SystemMessage
from langchain_core.prompts import ChatPromptTemplate, MessagesPlaceholder
from langchain_core.output_parsers import StrOutputParser
from langchain_core.runnables import RunnablePassthrough
from vector_store import VectorStoreManager
import json


class ConversationState(TypedDict):
    messages: Annotated[List[Dict], add_messages]
    context: Optional[str]
    document_ids: Optional[List[str]]
    query: str
    response: Optional[str]


class ConversationManager:
    def __init__(self, vector_store: VectorStoreManager):
        self.vector_store = vector_store
        self.llm = init_chat_model(
            "openai:gpt-4-0125-preview", temperature=0.7
        )
        self.conversations: Dict[str, List[Dict]] = {}

        self.rag_prompt = ChatPromptTemplate.from_messages(
            [
                (
                    "system",
                    """You are a helpful assistant with access to document content. 
            Use the provided context to answer questions accurately and naturally. 
            Provide clear, helpful responses based on the document information without mentioning 
            technical details like chunks or metadata.
            
            Document content:
            {context}""",
                ),
                MessagesPlaceholder(variable_name="chat_history"),
                ("human", "{query}"),
            ]
        )

        self.graph = self._build_graph()

    def _build_graph(self) -> StateGraph:
        graph_builder = StateGraph(ConversationState)

        graph_builder.add_node("retrieve_context", self._retrieve_context)
        graph_builder.add_node("generate_response", self._generate_response)

        graph_builder.add_edge(START, "retrieve_context")
        graph_builder.add_edge("retrieve_context", "generate_response")
        graph_builder.add_edge("generate_response", END)

        return graph_builder.compile()

    def _retrieve_context(self, state: ConversationState) -> Dict:
        query = state["query"]
        document_ids = state.get("document_ids")

        relevant_docs = self.vector_store.search_documents(
            query=query, k=5, document_ids=document_ids
        )

        # Simply concatenate document content without chunk information
        context_parts = []

        for doc in relevant_docs:
            # Clean content without metadata references
            context_parts.append(doc.page_content)

        context = "\n\n".join(context_parts)

        return {"context": context}

    def _generate_response(self, state: ConversationState) -> Dict:
        messages = state["messages"]
        context = state.get("context", "")
        query = state["query"]

        chat_history = []
        for msg in messages[:-1]:
            if msg["role"] == "user":
                chat_history.append(HumanMessage(content=msg["content"]))
            elif msg["role"] == "assistant":
                chat_history.append(AIMessage(content=msg["content"]))

        chain = self.rag_prompt | self.llm | StrOutputParser()

        response = chain.invoke(
            {"context": context, "chat_history": chat_history, "query": query}
        )

        return {"response": response}

    def chat(
        self,
        conversation_id: str,
        query: str,
        document_ids: Optional[List[str]] = None,
    ) -> Dict:

        if conversation_id not in self.conversations:
            self.conversations[conversation_id] = []

        messages = self.conversations[conversation_id].copy()
        messages.append({"role": "user", "content": query})

        result = self.graph.invoke(
            {
                "messages": messages,
                "query": query,
                "document_ids": document_ids,
                "context": None,
                "response": None,
            }
        )

        response = result.get("response", "")

        messages.append({"role": "assistant", "content": response})
        self.conversations[conversation_id] = messages

        return {
            "response": response,
            "sources": [],  # Empty sources array to maintain API compatibility
            "conversation_id": conversation_id,
        }

    def get_conversation_history(self, conversation_id: str) -> List[Dict]:
        return self.conversations.get(conversation_id, [])

    def clear_conversation(self, conversation_id: str) -> bool:
        if conversation_id in self.conversations:
            del self.conversations[conversation_id]
            return True
        return False
