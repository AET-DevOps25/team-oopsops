import axios from 'axios';
import type { AnonymizationRequestBody } from '@/types/anonymize';

const anonymizeApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/anonymization`,
});

export async function saveAnonymization(documentId: string,body: AnonymizationRequestBody) {
  const response = await anonymizeApi.post(`/${documentId}/add`, body);
  return response.data;
}

export async function downloadAnonymizedPdf(anonymizationId: string) {
  const response = await anonymizeApi.get(`${anonymizationId}/download`, {
    responseType: 'blob',
  });
  const url = window.URL.createObjectURL(response.data);
  const link = document.createElement("a");
  link.href = url;
  link.setAttribute("download", "anonymized_document.pdf");
  document.body.appendChild(link);
  link.click();
  link.remove();
}


