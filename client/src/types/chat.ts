export interface ChatMessage {
  role: 'user' | 'assistant';
  content: string;
  timestamp?: Date;
}

export interface ConversationRequest {
  conversation_id: string;
  query: string;
  document_ids?: string[];
}

export interface ConversationResponse {
  response: string;
  sources: Array<{
    filename: string;
    chunk_index: number;
    content: string;
  }>;
  conversation_id: string;
  status: string;
}

export interface DocumentUploadRequest {
  content: string;
  title?: string;
  metadata?: Record<string, string | number | boolean>;
}

export interface DocumentUploadResponse {
  document_id: string;
  filename: string;
  chunks_created: number;
  status: string;
}
