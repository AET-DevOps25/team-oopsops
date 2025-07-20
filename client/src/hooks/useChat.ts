import { useState, useCallback, useRef, useEffect } from 'react';
import { toast } from 'sonner';
import {
  ChatMessage,
  ConversationRequest,
  DocumentUploadRequest,
} from '@/types/chat';
import {
  uploadTextDocument,
  chatWithDocuments,
  clearConversation,
} from '@/services/chatService';

export const useChat = (
  documentContent?: string,
  documentTitle?: string,
  documentStatus: 'original' | 'anonymized' = 'original'
) => {
  const [messages, setMessages] = useState<ChatMessage[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [documentId, setDocumentId] = useState<string | null>(null);
  const [conversationId] = useState(
    () => `conv_${Date.now()}_${Math.random().toString(36).substr(2, 9)}`
  );
  const [isDocumentUploaded, setIsDocumentUploaded] = useState(false);

  const messagesEndRef = useRef<HTMLDivElement>(null);

  const scrollToBottom = () => {
    messagesEndRef.current?.scrollIntoView({ behavior: 'smooth' });
  };

  useEffect(() => {
    scrollToBottom();
  }, [messages]);

  // Reset chat when document content changes
  useEffect(() => {
    if (documentContent) {
      setIsDocumentUploaded(false);
      setDocumentId(null);
      // Don't clear messages immediately - let user decide if they want to clear
    }
  }, [documentContent]);

  // Upload document when content is provided
  const uploadDocument = useCallback(async () => {
    if (!documentContent || isDocumentUploaded) return;

    try {
      const request: DocumentUploadRequest = {
        content: documentContent,
        title: documentTitle || 'Document',
        metadata: {
          anonymized: documentStatus === 'anonymized',
          uploaded_from: 'editor',
          status: documentStatus,
        },
      };

      const response = await uploadTextDocument(request);
      setDocumentId(response.document_id);
      setIsDocumentUploaded(true);

      toast.success(
        `Document uploaded successfully! Created ${response.chunks_created} chunks for search.`
      );
    } catch (error) {
      console.error('Failed to upload document:', error);
      toast.error('Failed to upload document for chat. Please try again.');
    }
  }, [documentContent, documentTitle, documentStatus, isDocumentUploaded]);

  const sendMessage = useCallback(
    async (messageContent: string) => {
      if (!messageContent.trim() || isLoading) return;

      // If document hasn't been uploaded yet, try to upload it
      if (!isDocumentUploaded && documentContent) {
        await uploadDocument();
      }

      const userMessage: ChatMessage = {
        role: 'user',
        content: messageContent.trim(),
        timestamp: new Date(),
      };

      setMessages((prev) => [...prev, userMessage]);
      setIsLoading(true);

      try {
        const request: ConversationRequest = {
          conversation_id: conversationId,
          query: messageContent.trim(),
          document_ids: documentId ? [documentId] : undefined,
        };

        const response = await chatWithDocuments(request);

        const assistantMessage: ChatMessage = {
          role: 'assistant',
          content: response.response,
          timestamp: new Date(),
        };

        setMessages((prev) => [...prev, assistantMessage]);
      } catch (error) {
        console.error('Failed to send message:', error);
        toast.error('Failed to send message. Please try again.');

        // Remove the user message if the request failed
        setMessages((prev) => prev.slice(0, -1));
      } finally {
        setIsLoading(false);
      }
    },
    [
      isLoading,
      conversationId,
      documentId,
      documentContent,
      isDocumentUploaded,
      uploadDocument,
    ]
  );

  const clearChat = useCallback(async () => {
    try {
      await clearConversation(conversationId);
      setMessages([]);
      toast.success('Chat cleared successfully');
    } catch (error) {
      console.error('Failed to clear chat:', error);
      toast.error('Failed to clear chat');
    }
  }, [conversationId]);

  return {
    messages,
    isLoading,
    sendMessage,
    clearChat,
    messagesEndRef,
    isDocumentUploaded,
    uploadDocument,
  };
};
