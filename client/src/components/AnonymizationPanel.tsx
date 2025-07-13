import React from 'react';
import { Button } from '@/components/ui/button';
import { Slider } from '@/components/ui/slider';
import { Download } from 'lucide-react';
import { ScrollArea } from '@/components/ui/scroll-area';
import DocumentParagraph from './DocumentParagraph';
import { DocumentContent } from '@/types/documentContent';

type AnonymizationPanelProps = {
  anonymizationLevel: number;
  isAnonymized: boolean;
  hasManualEdits: boolean;
  documentData: DocumentContent;
  onAnonymizationLevelChange: (value: number[]) => void;
  onAnonymize: () => void;
  onDownload: (type: 'anonymized' | 'summary') => void;
  onTextSelection: () => void;
  onEditItem: (item: { id: string; text: string; replacement: string }) => void;
  getLevelDescription: (level: number) => string;
  isSaved: boolean;
  onSave: () => Promise<void>;
};

const AnonymizationPanel = ({
  anonymizationLevel,
  isAnonymized,
  hasManualEdits,
  documentData,
  onAnonymizationLevelChange,
  onAnonymize,
  onDownload,
  onTextSelection,
  onEditItem,
  getLevelDescription,
  isSaved,
  onSave,
}: AnonymizationPanelProps) => {
  const shouldShowAnonymized = isAnonymized || hasManualEdits;

  return (
    <div className="space-y-6">
      <div className="glass-panel p-6">
        <h2 className="text-xl font-semibold mb-4">Anonymization Settings</h2>

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
            onValueChange={onAnonymizationLevelChange}
          />
          <div className="flex justify-between mt-2 text-xs text-muted-foreground">
            <span>Light</span>
            <span>Medium</span>
            <span>Heavy</span>
          </div>
        </div>

        <div className="mb-6">
          <p className="text-sm text-muted-foreground">
            Select text in the document below to manually anonymize it, or use
            AI anonymization for automatic processing.
          </p>
        </div>

        <Button
          onClick={onAnonymize}
          className="w-full"
          disabled={isAnonymized}
        >
          {isAnonymized ? 'AI Anonymization Complete' : 'Use AI Anonymization'}
        </Button>
      </div>

      <div className="glass-panel p-6">
        <div className="flex items-center justify-between mb-4">
          <h2 className="text-xl font-semibold">Document Preview</h2>

          {shouldShowAnonymized && !isSaved && (
            <Button
              variant="outline"
              size="sm"
              onClick={onSave}
            >
              Save Anonymization
            </Button>
          )}

          {shouldShowAnonymized && isSaved && (
            <Button
              variant="outline"
              size="sm"
              onClick={() => onDownload('anonymized')}
            >
              <Download className="h-4 w-4 mr-2" />
              Download
            </Button>
          )}
        </div>

        <ScrollArea className="h-[400px]">
          <div className="bg-white dark:bg-gray-900 border border-border rounded-lg p-6">
            <h1 className="text-2xl font-bold mb-4">{documentData.title}</h1>
            <div className="prose max-w-none">
              <DocumentParagraph
                paragraph={{ paragraph: documentData.paragraph, sensitive: documentData.sensitive }}
                shouldShowAnonymized={shouldShowAnonymized}
                onTextSelection={onTextSelection}
                onEditItem={onEditItem}
              />
            </div>
          </div>
        </ScrollArea>
      </div>
    </div>
  );
};

export default AnonymizationPanel;
