import React, { useState, useEffect } from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '@/components/ui/tabs';
import { Slider } from '@/components/ui/slider';
import { Button } from '@/components/ui/button';
import { Download, Check, X } from 'lucide-react';
import { toast } from '@/hooks/use-toast';
import { ScrollArea } from '@/components/ui/scroll-area';
import { Input } from '@/components/ui/input';
import {
  Dialog,
  DialogContent,
  DialogDescription,
  DialogFooter,
  DialogHeader,
  DialogTitle,
} from '@/components/ui/dialog';

// TODO: Replace mock data with actual API calls
const mockDocument = {
  title: 'Financial Report 2024',
  content: [
    {
      paragraph:
        'This financial report was prepared by John Smith for Acme Corporation on April 15, 2024. It outlines the financial performance for Q1 2024.',
      sensitive: [
        { id: 's1', text: 'John Smith', replacement: 'Person A' },
        { id: 's2', text: 'Acme Corporation', replacement: 'Company X' },
        { id: 's3', text: 'April 15, 2024', replacement: '[DATE]' },
      ],
    },
    {
      paragraph:
        'For inquiries, please contact jane.doe@acmecorp.com or call at 555-123-4567. Our office is located at 123 Main Street, San Francisco, CA 94105.',
      sensitive: [
        { id: 's4', text: 'jane.doe@acmecorp.com', replacement: '[EMAIL]' },
        { id: 's5', text: '555-123-4567', replacement: '[PHONE]' },
        {
          id: 's6',
          text: '123 Main Street, San Francisco, CA 94105',
          replacement: '[ADDRESS]',
        },
      ],
    },
    {
      paragraph:
        'The company reported a total revenue of $5.2 million, which represents a 15% increase from the previous quarter. This growth was primarily driven by our new product line, which was launched in February 2024.',
      sensitive: [
        { id: 's7', text: '$5.2 million', replacement: '[AMOUNT]' },
        { id: 's8', text: '15%', replacement: '[PERCENTAGE]' },
        { id: 's9', text: 'February 2024', replacement: '[DATE]' },
      ],
    },
    {
      paragraph:
        'The board of directors, led by Robert Johnson, has approved an expansion plan for the next fiscal year. The company plans to open new offices in Chicago and Boston.',
      sensitive: [
        { id: 's10', text: 'Robert Johnson', replacement: 'Person B' },
        { id: 's11', text: 'Chicago', replacement: '[CITY A]' },
        { id: 's12', text: 'Boston', replacement: '[CITY B]' },
      ],
    },
  ],
  summary:
    "This financial report details Acme Corporation's Q1 2024 performance, highlighting a 15% revenue increase to $5.2 million, driven by new product launches. The board has approved expansion plans for new offices in Chicago and Boston.",
};

type DocumentEditorProps = {
  documentId?: string | null;
};

const DocumentEditor = ({ documentId }: DocumentEditorProps) => {
  const [activeTab, setActiveTab] = useState('anonymize');
  const [anonymizationLevel, setAnonymizationLevel] = useState(2);
  const [isAnonymized, setIsAnonymized] = useState(false);
  const [hasManualEdits, setHasManualEdits] = useState(false);
  const [documentData, setDocumentData] = useState<typeof mockDocument>({
    ...mockDocument,
  });
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [summary, setSummary] = useState('');
  const [selectedText, setSelectedText] = useState<string>('');
  const [editDialogOpen, setEditDialogOpen] = useState(false);
  const [currentEditItem, setCurrentEditItem] = useState<{
    id: string;
    text: string;
    replacement: string;
  } | null>(null);
  const [tempReplacement, setTempReplacement] = useState('');

  useEffect(() => {
    // In a real app, we would fetch the document from the server
    console.log('Fetching document with ID:', documentId);

    // For demo purposes, let's just use the mock data
    const documentInfo = sessionStorage.getItem('currentDocument');
    if (documentInfo) {
      const parsedInfo = JSON.parse(documentInfo);
      console.log('Current document info:', parsedInfo);
    }
  }, [documentId]);

  const handleAnonymizationLevelChange = (value: number[]) => {
    setAnonymizationLevel(value[0]);
  };

  const handleAnonymize = () => {
    // In a real app, we would call the API to anonymize the document
    toast({
      title: 'Processing document',
      description: 'Anonymizing document based on selected parameters...',
    });

    setTimeout(() => {
      setIsAnonymized(true);
      toast({
        title: 'Anonymization complete',
        description: 'Your document has been anonymized successfully.',
      });
    }, 2000);
  };

  const handleGenerateSummary = () => {
    setIsSummarizing(true);

    // In a real app, we would call the API to generate a summary
    setTimeout(() => {
      setIsSummarizing(false);
      setSummary(documentData.summary);
      toast({
        title: 'Summary generated',
        description: 'Document summary has been created successfully.',
      });
    }, 2000);
  };

  const handleDownload = (type: 'anonymized' | 'summary') => {
    // In a real app, we would generate and download the file
    toast({
      title: `Downloading ${type} document`,
      description: 'Your file will download shortly.',
    });
  };

  const getLevelDescription = (level: number) => {
    switch (level) {
      case 1:
        return 'Light - Only names and direct identifiers';
      case 2:
        return 'Medium - Names, contact details, and locations';
      case 3:
        return 'Heavy - All potential personal and sensitive information';
      default:
        return '';
    }
  };

  const handleTextSelection = () => {
    const selection = window.getSelection();
    if (selection && !selection.isCollapsed) {
      setSelectedText(selection.toString().trim());

      // Show options for anonymizing the selected text
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

  const handleSaveEdit = () => {
    if (!currentEditItem) return;

    const newDocumentData = { ...documentData };

    // Check if this is a new item or editing existing
    if (currentEditItem.id.startsWith('new-')) {
      // Find the paragraph containing the text and add new sensitive item
      for (let i = 0; i < newDocumentData.content.length; i++) {
        if (
          newDocumentData.content[i].paragraph.includes(currentEditItem.text)
        ) {
          newDocumentData.content[i].sensitive.push({
            id: currentEditItem.id,
            text: currentEditItem.text,
            replacement: tempReplacement,
          });
          break;
        }
      }
    } else {
      // Update existing item
      for (let i = 0; i < newDocumentData.content.length; i++) {
        const sensitiveIndex = newDocumentData.content[i].sensitive.findIndex(
          (s) => s.id === currentEditItem.id
        );

        if (sensitiveIndex >= 0) {
          newDocumentData.content[i].sensitive[sensitiveIndex].replacement =
            tempReplacement;
          break;
        }
      }
    }

    setDocumentData(newDocumentData);
    setHasManualEdits(true); // Mark that we have manual edits
    setEditDialogOpen(false);
    setCurrentEditItem(null);

    toast({
      title: 'Edit saved',
      description: 'The changes to anonymized text have been applied.',
    });
  };

  const handleCancelEdit = () => {
    setEditDialogOpen(false);
    setCurrentEditItem(null);
  };

  const renderParagraphWithHighlights = (paragraph: {
    paragraph: string;
    sensitive: { id: string; text: string; replacement: string }[];
  }) => {
    // Show anonymized view if either AI anonymization was run OR we have manual edits
    const shouldShowAnonymized = isAnonymized || hasManualEdits;

    if (!shouldShowAnonymized) {
      return (
        <p className="mb-4" onMouseUp={handleTextSelection}>
          {paragraph.paragraph}
        </p>
      );
    }

    let result = paragraph.paragraph;
    let segments = [];
    let lastIndex = 0;

    // Sort by position in text to process in order
    const sortedSensitive = [...paragraph.sensitive].sort(
      (a, b) =>
        paragraph.paragraph.indexOf(a.text) -
        paragraph.paragraph.indexOf(b.text)
    );

    for (const item of sortedSensitive) {
      const index = paragraph.paragraph.indexOf(item.text, lastIndex);
      if (index > -1) {
        // Add text before sensitive info
        if (index > lastIndex) {
          segments.push(
            <span
              key={`text-${lastIndex}-${index}`}
              onMouseUp={handleTextSelection}
            >
              {paragraph.paragraph.substring(lastIndex, index)}
            </span>
          );
        }

        // Add Git-style diff highlighting for anonymized text
        segments.push(
          <span
            key={`sensitive-${index}`}
            className="anonymized-pair group cursor-pointer relative"
            onClick={() => handleEditItem(item)}
          >
            <span className="text-sensitive">{item.text}</span>
            <span className="text-anonymized ml-1">{item.replacement}</span>
            <span className="absolute -top-8 left-1/2 transform -translate-x-1/2 bg-black text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity z-50 whitespace-nowrap pointer-events-none">
              Click to edit • Original → Replacement
            </span>
          </span>
        );

        lastIndex = index + item.text.length;
      }
    }

    // Add remaining text after the last sensitive info
    if (lastIndex < paragraph.paragraph.length) {
      segments.push(
        <span key={`text-${lastIndex}-end`} onMouseUp={handleTextSelection}>
          {paragraph.paragraph.substring(lastIndex)}
        </span>
      );
    }

    return <p className="mb-4">{segments}</p>;
  };

  return (
    <div className="w-full">
      <Tabs
        defaultValue="anonymize"
        value={activeTab}
        onValueChange={setActiveTab}
      >
        <TabsList className="mb-6">
          <TabsTrigger value="anonymize">Anonymize</TabsTrigger>
          <TabsTrigger value="summarize">Summarize</TabsTrigger>
        </TabsList>

        <TabsContent value="anonymize" className="space-y-6">
          <div className="glass-panel p-6">
            <h2 className="text-xl font-semibold mb-4">
              Anonymization Settings
            </h2>

            <div className="mb-8">
              <div className="flex justify-between items-center mb-2">
                <span className="font-medium">Anonymization Level</span>
                <span className="text-sm text-muted-foreground">
                  {getLevelDescription(anonymizationLevel)}
                </span>
              </div>
              <Slider
                defaultValue={[2]}
                max={3}
                min={1}
                step={1}
                value={[anonymizationLevel]}
                onValueChange={handleAnonymizationLevelChange}
              />
              <div className="flex justify-between mt-2 text-xs text-muted-foreground">
                <span>Light</span>
                <span>Medium</span>
                <span>Heavy</span>
              </div>
            </div>

            <div className="mb-6">
              <p className="text-sm text-muted-foreground">
                Select text in the document below to manually anonymize it, or
                use AI anonymization for automatic processing.
              </p>
            </div>

            <Button
              onClick={handleAnonymize}
              className="w-full"
              disabled={isAnonymized}
            >
              {isAnonymized
                ? 'AI Anonymization Complete'
                : 'Use AI Anonymization'}
            </Button>
          </div>

          <div className="glass-panel p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">Document Preview</h2>
              {(isAnonymized || hasManualEdits) && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleDownload('anonymized')}
                >
                  <Download className="h-4 w-4 mr-2" />
                  Download
                </Button>
              )}
            </div>

            <ScrollArea className="h-[400px]">
              <div className="bg-white dark:bg-gray-900 border border-border rounded-lg p-6">
                <h1 className="text-2xl font-bold mb-4">
                  {documentData.title}
                </h1>
                <div className="prose max-w-none">
                  {documentData.content.map((para, index) => (
                    <div key={index}>{renderParagraphWithHighlights(para)}</div>
                  ))}
                </div>
              </div>
            </ScrollArea>
          </div>
        </TabsContent>

        <TabsContent value="summarize" className="space-y-6">
          <div className="glass-panel p-6">
            <h2 className="text-xl font-semibold mb-4">
              Summarization Options
            </h2>
            <Button
              onClick={handleGenerateSummary}
              className="w-full"
              disabled={isSummarizing}
            >
              {isSummarizing ? 'Generating Summary...' : 'Generate Summary'}
            </Button>
          </div>

          <div className="glass-panel p-6">
            <div className="flex items-center justify-between mb-4">
              <h2 className="text-xl font-semibold">Document Summary</h2>
              {summary && (
                <Button
                  variant="outline"
                  size="sm"
                  onClick={() => handleDownload('summary')}
                >
                  <Download className="h-4 w-4 mr-2" />
                  Download
                </Button>
              )}
            </div>

            <div className="bg-white dark:bg-gray-900 border border-border rounded-lg p-6">
              {summary ? (
                <div className="prose max-w-none">
                  <p>{summary}</p>
                </div>
              ) : (
                <div className="text-center py-8 text-muted-foreground">
                  {isSummarizing ? (
                    <p>Generating summary...</p>
                  ) : (
                    <p>
                      Click "Generate Summary" to create a concise summary of
                      this document.
                    </p>
                  )}
                </div>
              )}
            </div>
          </div>
        </TabsContent>
      </Tabs>

      <Dialog open={editDialogOpen} onOpenChange={setEditDialogOpen}>
        <DialogContent className="sm:max-w-md">
          <DialogHeader>
            <DialogTitle>Edit Anonymized Text</DialogTitle>
            <DialogDescription>
              Customize how this text will appear in the anonymized document.
            </DialogDescription>
          </DialogHeader>
          {currentEditItem && (
            <div className="space-y-4 py-4">
              <div className="space-y-2">
                <p className="text-sm font-medium">Original Text:</p>
                <div className="text-sensitive p-2 bg-muted/50 rounded">
                  {currentEditItem.text}
                </div>
              </div>
              <div className="space-y-2">
                <p className="text-sm font-medium">Replacement Text:</p>
                <Input
                  value={tempReplacement}
                  onChange={(e) => setTempReplacement(e.target.value)}
                  placeholder="Enter replacement text"
                  className="text-anonymized"
                />
              </div>
            </div>
          )}
          <DialogFooter className="flex space-x-2 sm:justify-end">
            <Button type="button" variant="outline" onClick={handleCancelEdit}>
              <X className="mr-2 h-4 w-4" />
              Cancel
            </Button>
            <Button type="button" onClick={handleSaveEdit}>
              <Check className="mr-2 h-4 w-4" />
              Save Changes
            </Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
};

export default DocumentEditor;
