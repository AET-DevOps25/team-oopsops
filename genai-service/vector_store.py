# ABOUTME: This module manages the vector database for document embeddings and retrieval
# ABOUTME: It provides functionality to store, search, and retrieve document chunks for RAG

from typing import List, Dict, Optional, Tuple
import os
import chromadb
from chromadb.config import Settings
from langchain_chroma import Chroma
from langchain_openai import OpenAIEmbeddings
from langchain.text_splitter import RecursiveCharacterTextSplitter
from langchain_community.document_loaders import PyPDFLoader, TextLoader
from langchain.schema import Document
import hashlib
from datetime import datetime


class VectorStoreManager:
    def __init__(self, persist_directory: str = "./chroma_db"):
        self.persist_directory = persist_directory
        os.makedirs(persist_directory, exist_ok=True)
        
        self.embeddings = OpenAIEmbeddings(model="text-embedding-3-small")
        
        self.client = chromadb.PersistentClient(
            path=persist_directory,
            settings=Settings(
                anonymized_telemetry=False,
                allow_reset=True
            )
        )
        
        self.collection_name = "documents"
        self.vectorstore = Chroma(
            client=self.client,
            collection_name=self.collection_name,
            embedding_function=self.embeddings,
            persist_directory=persist_directory
        )
        
        self.text_splitter = RecursiveCharacterTextSplitter(
            chunk_size=1000,
            chunk_overlap=200,
            length_function=len,
            separators=["\n\n", "\n", ".", "!", "?", ",", " ", ""]
        )
        
        self.document_metadata: Dict[str, Dict] = {}
    
    def generate_document_id(self, filename: str, content: bytes) -> str:
        content_hash = hashlib.md5(content).hexdigest()[:8]
        return f"{filename}_{content_hash}"
    
    def ingest_pdf(self, file_path: str, filename: str, document_id: Optional[str] = None) -> Tuple[str, int]:
        if not document_id:
            with open(file_path, 'rb') as f:
                content = f.read()
            document_id = self.generate_document_id(filename, content)
        
        loader = PyPDFLoader(file_path)
        documents = loader.load()
        
        if not documents:
            raise ValueError(f"No content could be extracted from PDF: {filename}")
        
        # Filter out empty documents
        documents = [doc for doc in documents if doc.page_content.strip()]
        
        if not documents:
            raise ValueError(f"PDF contains no readable text: {filename}")
        
        chunks = self.text_splitter.split_documents(documents)
        
        # Filter out empty chunks
        chunks = [chunk for chunk in chunks if chunk.page_content.strip()]
        
        if not chunks:
            raise ValueError(f"No text chunks could be created from PDF: {filename}")
        
        for i, chunk in enumerate(chunks):
            chunk.metadata.update({
                "document_id": document_id,
                "filename": filename,
                "chunk_index": i,
                "total_chunks": len(chunks),
                "upload_time": datetime.utcnow().isoformat(),
                "file_type": "pdf"
            })
        
        self.vectorstore.add_documents(chunks)
        
        self.document_metadata[document_id] = {
            "filename": filename,
            "file_type": "pdf",
            "total_chunks": len(chunks),
            "upload_time": datetime.utcnow().isoformat()
        }
        
        return document_id, len(chunks)
    
    def ingest_markdown(self, content: str, filename: str, document_id: Optional[str] = None) -> Tuple[str, int]:
        if not content.strip():
            raise ValueError(f"Markdown content is empty: {filename}")
        
        if not document_id:
            document_id = self.generate_document_id(filename, content.encode())
        
        doc = Document(page_content=content, metadata={"source": filename})
        chunks = self.text_splitter.split_documents([doc])
        
        # Filter out empty chunks
        chunks = [chunk for chunk in chunks if chunk.page_content.strip()]
        
        if not chunks:
            raise ValueError(f"No text chunks could be created from markdown: {filename}")
        
        for i, chunk in enumerate(chunks):
            chunk.metadata.update({
                "document_id": document_id,
                "filename": filename,
                "chunk_index": i,
                "total_chunks": len(chunks),
                "upload_time": datetime.utcnow().isoformat(),
                "file_type": "markdown"
            })
        
        self.vectorstore.add_documents(chunks)
        
        self.document_metadata[document_id] = {
            "filename": filename,
            "file_type": "markdown",
            "total_chunks": len(chunks),
            "upload_time": datetime.utcnow().isoformat()
        }
        
        return document_id, len(chunks)
    
    def search_documents(self, query: str, k: int = 5, document_ids: Optional[List[str]] = None) -> List[Document]:
        search_kwargs = {"k": k}
        
        if document_ids:
            search_kwargs["filter"] = {"document_id": {"$in": document_ids}}
        
        return self.vectorstore.similarity_search(query, **search_kwargs)
    
    def get_retriever(self, k: int = 5, document_ids: Optional[List[str]] = None):
        search_kwargs = {"k": k}
        
        if document_ids:
            search_kwargs["filter"] = {"document_id": {"$in": document_ids}}
        
        return self.vectorstore.as_retriever(search_kwargs=search_kwargs)
    
    def list_documents(self) -> List[Dict]:
        return [
            {"document_id": doc_id, **metadata}
            for doc_id, metadata in self.document_metadata.items()
        ]
    
    def delete_document(self, document_id: str) -> bool:
        try:
            collection = self.client.get_collection(self.collection_name)
            collection.delete(where={"document_id": document_id})
            
            if document_id in self.document_metadata:
                del self.document_metadata[document_id]
            
            return True
        except Exception:
            return False