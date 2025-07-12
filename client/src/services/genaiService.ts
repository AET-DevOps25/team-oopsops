import axios from 'axios';

const genaiApi = axios.create({
  baseURL: `${import.meta.env.VITE_API_URL}/api/v1/genai`,
});


export async function anonymizeDocument(originalText: string, level: string){
  const response = await genaiApi.post("/anonymize", {
    originalText,
    level,
  });
  return response.data;
}

export async function summarize(originalText: string, level: string) {
  const response = await genaiApi.post('/summarize', {
    originalText,
    level,
  });
  return response.data;
}
