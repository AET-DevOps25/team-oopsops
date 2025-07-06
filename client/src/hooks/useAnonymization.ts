import { useState } from 'react';
import { toast } from '@/hooks/use-toast';
import { DocumentContent } from '@/data/mockDocument';

export const useAnonymization = (
  documentData: DocumentContent,
  setDocumentData: (data: DocumentContent) => void
) => {
  const [anonymizationLevel, setAnonymizationLevel] = useState(2);
  const [isAnonymized, setIsAnonymized] = useState(false);
  const [hasManualEdits, setHasManualEdits] = useState(false);

  const handleAnonymizationLevelChange = (value: number[]) => {
    setAnonymizationLevel(value[0]);
  };

  const handleAnonymize = () => {
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

  const handleSaveEdit = (
    currentEditItem: { id: string; text: string; replacement: string },
    tempReplacement: string
  ) => {
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
    setHasManualEdits(true);

    toast({
      title: 'Edit saved',
      description: 'The changes to anonymized text have been applied.',
    });
  };

  const handleDownload = (type: 'anonymized' | 'summary') => {
    toast({
      title: `Downloading ${type} document`,
      description: 'Your file will download shortly.',
    });
  };

  return {
    anonymizationLevel,
    isAnonymized,
    hasManualEdits,
    handleAnonymizationLevelChange,
    handleAnonymize,
    getLevelDescription,
    handleSaveEdit,
    handleDownload,
  };
};
