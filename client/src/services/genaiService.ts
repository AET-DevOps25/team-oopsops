import genaiApi from "@/api/genaiApi";

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
