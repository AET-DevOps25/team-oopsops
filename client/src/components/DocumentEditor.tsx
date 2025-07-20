import { useState, useEffect } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { useAnonymization } from '@/hooks/useAnonymization';
import { useSummarization } from '@/hooks/useSummarization';
import AnonymizationPanel from './AnonymizationPanel';
import SummarizationPanel from './SummarizationPanel';
import EditAnonymizedDialog from './EditAnonymizedDialog';
import ChatWidget from './ChatWidget';
import { DocumentContent } from '@/types/documentContent';
import { Document } from '@/types/document';
import { toast } from 'sonner';

type DocumentEditorProps = {
  document?: Document | undefined;
};

const DocumentEditor = ({ document }: DocumentEditorProps) => {
  const [activeTab, setActiveTab] = useState('anonymize');
  const [documentData, setDocumentData] = useState<DocumentContent>();
  const [selectedText, setSelectedText] = useState<string>('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [currentEditItem, setCurrentEditItem] = useState<{
    id: string;
    text: string;
    replacement: string;
  } | null>(null);
  const [tempReplacement, setTempReplacement] = useState('');
  const [isAnonymized, setIsAnonymized] = useState(false);
  const [isSaved, setIsSaved] = useState(false);

  // Custom hooks
  const {
    anonymizationLevel,
    hasManualEdits,
    handleAnonymizationLevelChange,
    handleAnonymize,
    getLevelDescription,
    handleSaveEdit,
    handleDownload,
    handleSave,
  } = useAnonymization(
    documentData,
    setDocumentData,
    isAnonymized,
    setIsAnonymized,
    isSaved,
    setIsSaved
  );

  const {
    summarizationLevel,
    isSummarizing,
    summary,
    handleSummarizationLevel,
    handleGenerateSummary,
    getSummarizationLevelDescription,
    handleDownloadSummary,
  } = useSummarization(documentData, setDocumentData);

  useEffect(() => {
    if (documentData) {
      console.log('Current documentData:', documentData);
    }
  }, [documentData]);

  useEffect(() => {
    console.log('Fetching document with ID:', document);

    const documentInfo = sessionStorage.getItem('currentDocument');
    if (document) {
      const realContent: DocumentContent = {
        title: document.fileName?.replace(/\.pdf$/i, '') || 'Untitled',
        paragraph: document.documentText || '',
        sensitive: [],
        summary: '',
      };
      setDocumentData(realContent);
    } else {
      const documentInfo = sessionStorage.getItem('currentDocument');
      if (documentInfo) {
        const parsedInfo = JSON.parse(documentInfo);
        const realContent: DocumentContent = {
          title: parsedInfo.fileName?.replace(/\.pdf$/i, '') || 'Untitled',
          paragraph: parsedInfo.documentText || '',
          sensitive: [],
          summary: '',
        };
        setDocumentData(realContent);
      }
    }
  }, [document]);

  if (!documentData) {
    return <div className="p-4 text-muted-foreground">Loading document...</div>;
  }

  const handleTextSelection = () => {
    const selection = window.getSelection();
    if (selection && !selection.isCollapsed) {
      setSelectedText(selection.toString().trim());

      if (selection.toString().trim().length > 0) {
        setCurrentEditItem({
          id: `new-${Date.now()}`,
          text: selection.toString().trim(),
          replacement: '[REDACTED]',
        });
        setTempReplacement('[REDACTED]');
        setEditDialogOpen(true);
      }
    }
  };

  const handleEditItem = (item: {
    id: string;
    text: string;
    replacement: string;
  }) => {
    setCurrentEditItem(item);
    setTempReplacement(item.replacement);
    setEditDialogOpen(true);
  };

  const handleSaveEditInternal = () => {
    if (!currentEditItem) return;
    handleSaveEdit(currentEditItem, tempReplacement);
    setEditDialogOpen(false);
    setCurrentEditItem(null);
  };

  const handleCancelEdit = () => {
    setEditDialogOpen(false);
    setCurrentEditItem(null);
  };

  // Get the current document content for the chat widget
  const getCurrentDocumentContent = () => {
    if (!documentData) return '';

    // Return the current paragraph content (could be original or anonymized)
    return documentData.paragraph;
  };

  const getDocumentStatus = () => {
    if (isAnonymized || hasManualEdits) {
      return 'anonymized';
    }
    return 'original';
  };
  return (
    <div className="w-full">
      <Tabs
        defaultValue="anonymize"
        value={activeTab}
        onValueChange={(value) => {
          if (value === 'summarize' && !isAnonymized) {
            toast.warning('Please anonymize your document before summarizing.');
            return;
          }
          setActiveTab(value);
        }}
      >
        <TabsList className="mb-6">
          <TabsTrigger value="anonymize">Anonymize</TabsTrigger>
          <TabsTrigger
            value="summarize"
            className={
              !isAnonymized
                ? 'pointer-events-auto text-muted-foreground opacity-50'
                : ''
            }
          >
            Summarize
          </TabsTrigger>
        </TabsList>

        <TabsContent value="anonymize">
          <AnonymizationPanel
            anonymizationLevel={anonymizationLevel}
            isAnonymized={isAnonymized}
            hasManualEdits={hasManualEdits}
            documentData={documentData}
            onAnonymizationLevelChange={handleAnonymizationLevelChange}
            onAnonymize={handleAnonymize}
            onDownload={handleDownload}
            onTextSelection={handleTextSelection}
            onEditItem={handleEditItem}
            getLevelDescription={getLevelDescription}
            onSave={handleSave}
            isSaved={isSaved}
          />
        </TabsContent>

        <TabsContent value="summarize">
          <SummarizationPanel
            summarizationLevel={summarizationLevel}
            isSummarizing={isSummarizing}
            summary={summary}
            onGenerateSummary={handleGenerateSummary}
            onSummarizationLevelChange={handleSummarizationLevel}
            getLevelDescription={getSummarizationLevelDescription}
            onDownload={handleDownloadSummary}
          />
        </TabsContent>
      </Tabs>

      <EditAnonymizedDialog
        isOpen={editDialogOpen}
        onClose={() => setEditDialogOpen(false)}
        currentEditItem={currentEditItem}
        tempReplacement={tempReplacement}
        onReplacementChange={setTempReplacement}
        onSave={handleSaveEditInternal}
        onCancel={handleCancelEdit}
      />

      {/* Chat Widget - available for any document content */}
      <ChatWidget
        documentContent={getCurrentDocumentContent()}
        documentTitle={documentData?.title}
        documentStatus={getDocumentStatus()}
        isVisible={!!documentData} // Show whenever document is loaded
      />
    </div>
  );
};

export default DocumentEditor;
