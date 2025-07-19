// ABOUTME: Document type definition matching backend DocumentDto structure
// ABOUTME: Includes all fields returned by the document service API
export interface Document {
  id: string;
  userId: string;
  fileName: string;
  fileUrl: string;
  status: string;
  uploadDate: string; // ISO string from backend Instant
  documentText: string;
}
