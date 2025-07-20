// ABOUTME: Document service for API communication with backend document endpoints
// ABOUTME: Handles document upload and retrieval with proper typing and error handling
import documentApi from '@/api/documentApi';
import { Document } from '@/types/document';

export async function uploadDocument(file: File): Promise<Document> {
  const form = new FormData();
  form.append('file', file);
  const response = await documentApi.post<Document>('/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
}

export async function fetchDocuments(): Promise<Document[]> {
  const response = await documentApi.get<Document[]>('/');
  return response.data;
}
