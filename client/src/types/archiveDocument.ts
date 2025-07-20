// ABOUTME: Unified document type for archive view combining original and anonymized documents
// ABOUTME: Provides consistent interface for displaying both document types in the archive
export interface ArchiveDocument {
  id: string;
  fileName: string;
  uploadDate: string;
  status: 'Original' | 'Anonymized';
  documentType: 'original' | 'anonymized';
  originalDocumentId?: string; // For anonymized docs, reference to original
  documentText: string;
}
