import axios from 'axios';
import type { Document } from '@/types/document';

const anonymizationApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/anonymization`,
});

export async function anonymizeDocument(file: File): Promise<Anonymization> {
  export async function anonymizeText(originalText: string, level: string = "MEDIUM"): Promise<string> {
  const response = await genaiApi.post("/anonymize", {
    originalText,
    level,
  });
  return response.data.anonymizedText;
}
