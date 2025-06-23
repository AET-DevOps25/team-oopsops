import axios from 'axios';
import type { Document } from '@/types/document';

const documentApi = axios.create({
  baseURL: '/api/v1/documents',
});

export async function uploadDocument(file: File): Promise<Document> {
  const form = new FormData();
  form.append('file', file);
  const response = await documentApi.post<Document>('/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
}
