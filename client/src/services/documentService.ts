import documentApi from "@/api/documentApi";

export async function uploadDocument(file: File): Promise<Document> {
  const form = new FormData();
  form.append("file", file);
  const response = await documentApi.post<Document>("/upload", form, {
    headers: { "Content-Type": "multipart/form-data" },
  });
  return response.data;
}

export async function fetchDocuments(): Promise<Document[]> {
  const response = await documentApi.get<Document[]>("/");
  return response.data;
}