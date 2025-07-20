import anonymizeApi from '@/api/anonymizeApi';
import type {
  AnonymizationRequestBody,
  AnonymizationDto,
} from '@/types/anonymize';

export async function saveAnonymization(
  documentId: string,
  body: AnonymizationRequestBody
): Promise<AnonymizationDto> {
  const { data } = await anonymizeApi.post<AnonymizationDto>(
    `/${documentId}/add`,
    body
  );
  return data;
}

export async function downloadAnonymizedPdf(anonymizationId: string) {
  const response = await anonymizeApi.get(`${anonymizationId}/download`, {
    responseType: 'blob',
  });
  const url = window.URL.createObjectURL(response.data);
  const link = document.createElement('a');
  link.href = url;
  link.setAttribute('download', 'anonymized_document.pdf');
  document.body.appendChild(link);
  link.click();
  link.remove();
}

export async function fetchAnonymizations(): Promise<AnonymizationDto[]> {
  const { data } = await anonymizeApi.get<AnonymizationDto[]>('/');
  return data;
}
