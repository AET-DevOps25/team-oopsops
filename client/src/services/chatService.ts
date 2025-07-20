import chatApi from '@/api/chatApi';
import {
  ConversationRequest,
  ConversationResponse,
  DocumentUploadRequest,
  DocumentUploadResponse,
  ChatMessage,
} from '@/types/chat';

export async function uploadTextDocument(
  request: DocumentUploadRequest
): Promise<DocumentUploadResponse> {
  const response = await chatApi.post<DocumentUploadResponse>(
    '/documents/upload-text',
    request
  );
  return response.data;
}

export async function chatWithDocuments(
  request: ConversationRequest
): Promise<ConversationResponse> {
  const response = await chatApi.post<ConversationResponse>(
    '/conversation/chat',
    request
  );
  return response.data;
}

export async function getConversationHistory(
  conversationId: string
): Promise<{ conversation_id: string; history: ChatMessage[] }> {
  const response = await chatApi.get(`/conversation/${conversationId}/history`);
  return response.data;
}

export async function clearConversation(
  conversationId: string
): Promise<{ status: string; message: string }> {
  const response = await chatApi.delete(`/conversation/${conversationId}`);
  return response.data;
}
