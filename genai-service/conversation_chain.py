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
    sources: Optional[List[Dict]]


class ConversationManager:
    def __init__(self, vector_store: VectorStoreManager):
        self.vector_store = vector_store
        self.llm = init_chat_model("openai:gpt-4-0125-preview", temperature=0.7)
        self.conversations: Dict[str, List[Dict]] = {}
        
        self.rag_prompt = ChatPromptTemplate.from_messages([
            ("system", """You are a helpful assistant with access to documents. 
            Use the provided context to answer questions accurately. When you use information from the context, 
            mention which document it came from by referencing the filename.
            
            Context from documents:
            {context}
            
            Remember to cite your sources when using document information."""),
            MessagesPlaceholder(variable_name="chat_history"),
            ("human", "{query}")
        ])
        
        self.graph = self._build_graph()
    
    def _build_graph(self) -> StateGraph:
        graph_builder = StateGraph(ConversationState)
        
        graph_builder.add_node("retrieve_context", self._retrieve_context)
        graph_builder.add_node("generate_response", self._generate_response)
        graph_builder.add_node("extract_sources", self._extract_sources)
        
        graph_builder.add_edge(START, "retrieve_context")
        graph_builder.add_edge("retrieve_context", "generate_response")
        graph_builder.add_edge("generate_response", "extract_sources")
        graph_builder.add_edge("extract_sources", END)
        
        return graph_builder.compile()
    
    def _retrieve_context(self, state: ConversationState) -> Dict:
        query = state["query"]
        document_ids = state.get("document_ids")
        
        relevant_docs = self.vector_store.search_documents(
            query=query,
            k=5,
            document_ids=document_ids
        )
        
        context_parts = []
        sources = []
        
        for doc in relevant_docs:
            filename = doc.metadata.get("filename", "Unknown")
            chunk_index = doc.metadata.get("chunk_index", 0)
            document_id = doc.metadata.get("document_id", "")
            
            context_parts.append(f"[From {filename}, chunk {chunk_index}]:\n{doc.page_content}")
            
            sources.append({
                "document_id": document_id,
                "filename": filename,
                "chunk_index": chunk_index,
                "content": doc.page_content[:200] + "..."
            })
        
        context = "\n\n".join(context_parts)
        
        return {"context": context, "sources": sources}
    
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
        
        response = chain.invoke({
            "context": context,
            "chat_history": chat_history,
            "query": query
        })
        
        return {"response": response}
    
    def _extract_sources(self, state: ConversationState) -> Dict:
        response = state.get("response", "")
        sources = state.get("sources", [])
        
        referenced_files = set()
        for source in sources:
            filename = source.get("filename", "")
            if filename and filename in response:
                referenced_files.add(filename)
        
        filtered_sources = [
            source for source in sources
            if source.get("filename") in referenced_files
        ]
        
        return {"sources": filtered_sources}
    
    def chat(self, 
             conversation_id: str,
             query: str,
             document_ids: Optional[List[str]] = None) -> Dict:
        
        if conversation_id not in self.conversations:
            self.conversations[conversation_id] = []
        
        messages = self.conversations[conversation_id].copy()
        messages.append({"role": "user", "content": query})
        
        result = self.graph.invoke({
            "messages": messages,
            "query": query,
            "document_ids": document_ids,
            "context": None,
            "response": None,
            "sources": None
        })
        
        response = result.get("response", "")
        sources = result.get("sources", [])
        
        messages.append({"role": "assistant", "content": response})
        self.conversations[conversation_id] = messages
        
        return {
            "response": response,
            "sources": sources,
            "conversation_id": conversation_id
        }
    
    def get_conversation_history(self, conversation_id: str) -> List[Dict]:
        return self.conversations.get(conversation_id, [])
    
    def clear_conversation(self, conversation_id: str) -> bool:
        if conversation_id in self.conversations:
            del self.conversations[conversation_id]
            return True
        return False