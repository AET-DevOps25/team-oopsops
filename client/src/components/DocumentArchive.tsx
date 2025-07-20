// ABOUTME: Document archive component that displays user's documents from backend API
// ABOUTME: Includes filtering, loading states, and proper error handling for document management
import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { Button } from '@/components/ui/button';
import DocumentStatusFilter from '@/components/DocumentStatusFilter';
import { fetchDocuments } from '@/services/documentService';
import { fetchAnonymizations } from '@/services/anonymizeService';
import { Document } from '@/types/document';
import { ArchiveDocument } from '@/types/archiveDocument';

import { useToast } from '@/hooks/use-toast';

type DocumentStatus = 'All' | 'Anonymized' | 'Original';

const DocumentArchive = () => {
  const [selectedStatus, setSelectedStatus] = useState<DocumentStatus>('All');
  const [documents, setDocuments] = useState<ArchiveDocument[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const { toast } = useToast();

  useEffect(() => {
    const loadDocuments = async () => {
      try {
        setLoading(true);
        setError(null);

        // Fetch both original documents and anonymizations in parallel
        const [originalDocs, anonymizations] = await Promise.all([
          fetchDocuments(),
          fetchAnonymizations().catch((err) => {
            console.warn('Failed to fetch anonymizations:', err);
            // Show a warning toast but don't fail the entire operation
            toast({
              title: 'Warning',
              description:
                'Could not load anonymized documents. Showing original documents only.',
              variant: 'default',
            });
            return [];
          }),
        ]);

        // Convert original documents to archive format
        const archiveOriginals: ArchiveDocument[] = originalDocs.map((doc) => ({
          id: doc.id,
          fileName: doc.fileName,
          uploadDate: doc.uploadDate,
          status: 'Original' as const,
          documentType: 'original' as const,
          documentText: doc.documentText,
        }));

        // Convert anonymizations to archive format
        const archiveAnonymized: ArchiveDocument[] = anonymizations.map(
          (anon) => ({
            id: anon.id,
            fileName: `${getOriginalFileName(
              originalDocs,
              anon.documentId
            )} (Anonymized)`,
            uploadDate: anon.created,
            status: 'Anonymized' as const,
            documentType: 'anonymized' as const,
            originalDocumentId: anon.documentId,
            documentText: anon.anonymizedText,
          })
        );

        // Combine and sort by upload date (newest first)
        const allDocuments = [...archiveOriginals, ...archiveAnonymized].sort(
          (a, b) =>
            new Date(b.uploadDate).getTime() - new Date(a.uploadDate).getTime()
        );

        setDocuments(allDocuments);
      } catch (err) {
        const errorMessage =
          err instanceof Error ? err.message : 'Failed to load documents';
        setError(errorMessage);
        toast({
          title: 'Error',
          description: errorMessage,
          variant: 'destructive',
        });
      } finally {
        setLoading(false);
      }
    };

    loadDocuments();
  }, [toast]);

  // Helper function to get original document filename for anonymized documents
  const getOriginalFileName = (
    originalDocs: Document[],
    documentId: string
  ): string => {
    const originalDoc = originalDocs.find((doc) => doc.id === documentId);
    return originalDoc?.fileName || 'Unknown Document';
  };

  const formatDate = (dateString: string) => {
    try {
      return new Date(dateString).toLocaleDateString();
    } catch {
      return 'Invalid date';
    }
  };

  // Filter documents based on selected status

  const filteredDocuments =
    selectedStatus === 'All'
      ? documents
      : documents.filter((doc) => doc.status === selectedStatus);

  if (loading) {
    return (
      <div className="space-y-6">
        <DocumentStatusFilter
          selectedStatus={selectedStatus}
          onStatusChange={setSelectedStatus}
        />
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {[1, 2, 3].map((i) => (
            <div key={i} className="glass-panel p-6 rounded-xl animate-pulse">
              <div className="h-6 bg-gray-200 rounded mb-2"></div>
              <div className="flex justify-between mb-4">
                <div className="h-4 bg-gray-200 rounded w-20"></div>
                <div className="h-4 bg-gray-200 rounded w-16"></div>
              </div>
              <div className="h-10 bg-gray-200 rounded"></div>
            </div>
          ))}
        </div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="text-center py-10 glass-panel">
        <h3 className="text-lg font-medium mb-2 text-red-600">
          Error Loading Documents
        </h3>
        <p className="text-muted-foreground mb-4">{error}</p>
        <Button onClick={() => window.location.reload()}>Try Again</Button>
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <DocumentStatusFilter
        selectedStatus={selectedStatus}
        onStatusChange={setSelectedStatus}
      />

      {filteredDocuments.length > 0 ? (
        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-6">
          {filteredDocuments.map((doc) => (
            <div
              key={doc.id}
              className="glass-panel p-6 rounded-xl transition-all duration-300 hover:scale-105 hover:shadow-lg hover:shadow-primary/10 hover:border-primary/20 cursor-pointer group"
            >
              <h3 className="font-medium text-lg mb-2 group-hover:text-primary transition-colors duration-200 break-words word-wrap">
                {doc.fileName}
              </h3>
              <div className="flex justify-between text-sm text-muted-foreground mb-4">
                <span>{formatDate(doc.uploadDate)}</span>
                <span
                  className={`px-2 py-0.5 rounded-full transition-all duration-200 ${doc.status === 'Anonymized'
                      ? 'bg-green-100 text-green-800 group-hover:bg-green-200'
                      : 'bg-gray-100 text-gray-800 group-hover:bg-gray-200'
                    }`}
                >
                  {doc.status}
                </span>
              </div>
              <Link
                to="/editor"
                state={{ document: doc }}
              >
                <Button className="w-full transition-all duration-200 group-hover:bg-primary/90">
                  {doc.documentType === 'anonymized' ? 'View Anonymized' : 'Open Document'}
                </Button>
              </Link>
            </div>
          ))}
        </div>
      ) : (
        <div className="text-center py-10 glass-panel">
          <h3 className="text-lg font-medium mb-2">No documents found</h3>
          <p className="text-muted-foreground mb-4">
            {selectedStatus === 'All'
              ? "You haven't uploaded any documents yet."
              : `No ${selectedStatus.toLowerCase()} documents found.`}
          </p>
          <Link to="/home">
            <Button>Upload a document</Button>
          </Link>
        </div>
      )}
    </div>
  );
};

export default DocumentArchive;
