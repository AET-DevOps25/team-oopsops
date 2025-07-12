import axios, { AxiosHeaders, InternalAxiosRequestConfig } from 'axios';

const documentApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/documents`
});

documentApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('access_token');
  if (token) {
    const headers = new AxiosHeaders(config.headers);
    headers.set('Authorization', `Bearer ${token}`);
    config.headers = headers;
  }
  return config;
});

export async function uploadDocument(file: File): Promise<Document> {
  const form = new FormData();
  form.append('file', file);
  const response = await documentApi.post<Document>('/upload', form, {
    headers: { 'Content-Type': 'multipart/form-data' },
  });
  return response.data;
}
