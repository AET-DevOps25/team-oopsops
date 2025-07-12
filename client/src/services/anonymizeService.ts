import axios, { AxiosHeaders, InternalAxiosRequestConfig } from 'axios';
import type { AnonymizationRequestBody } from '@/types/anonymize';

const anonymizeApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/anonymization`,
});

// Attach the Bearer token on every request
anonymizeApi.interceptors.request.use((config: InternalAxiosRequestConfig) => {
  const token = localStorage.getItem('access_token');
  if (token) {
    const headers = new AxiosHeaders(config.headers);
    headers.set('Authorization', `Bearer ${token}`);
    config.headers = headers;
  }
  return config;
});

export async function saveAnonymization(
  documentId: string,
  body: AnonymizationRequestBody
) {
  const response = await anonymizeApi.post<unknown, { data: unknown }>(
    `/${documentId}/add`,
    body
  );
  return response.data;
}

export async function downloadAnonymizedPdf(anonymizationId: string) {
  const response = await anonymizeApi.get<Blob>(
    `/${anonymizationId}/download`,
    { responseType: 'blob' }
  );

  const url = window.URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'anonymized_document.pdf');
  document.body.appendChild(link);
  link.click();
  link.remove();
}


