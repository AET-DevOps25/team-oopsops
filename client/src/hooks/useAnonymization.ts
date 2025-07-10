import { useState, useEffect } from "react";
import { toast } from "sonner";
import { DocumentContent } from "@/types/documentContent";
import { anonymizeDocument } from '@/services/genaiService';


const levelMap = {
  1: "light",
  2: "medium",
  3: "high",
} as const;

export const useAnonymization = (
  documentData: DocumentContent,
  setDocumentData: (data: DocumentContent) => void,
  isAnonymized: boolean,
  setIsAnonymized: (value: boolean) => void
) => {
  const [anonymizationLevel, setAnonymizationLevel] = useState(2);
  const [hasManualEdits, setHasManualEdits] = useState(false);

  const handleAnonymizationLevelChange = (value: number[]) => {
    setAnonymizationLevel(value[0]);
  };


  const handleAnonymize = async () => {
    toast.info("Processing document", {
      description: "Anonymizing document based on selected parameters...",
    });

    try {
      const fullText = documentData.paragraph;

      const response = await anonymizeDocument(fullText, levelMap[anonymizationLevel]);
      console.log("Anonymization response:", response.responseText, response.changedTerms);
      setDocumentData({
        ...documentData,
        paragraph: response.responseText,
        sensitive: (response.changedTerms || []).map((term, index) => ({
          id: `term-${Date.now()}-${index}`,
          text: term.original,
          replacement: term.anonymized,
        })),
      });

      setIsAnonymized(true);
      toast.success("Anonymization complete", {
        description: "Your document has been anonymized successfully.",
      });
    } catch (err) {
      toast.error("Anonymization failed", {
        description: "Something went wrong while contacting the backend.",
      });
      console.error("Anonymization error:", err);
    }
  };



  const getLevelDescription = (level: number) => {
    switch (level) {
      case 1:
        return "Light - Only names and direct identifiers";
      case 2:
        return "Medium - Names, contact details, and locations";
      case 3:
        return "Heavy - All potential personal and sensitive information";
      default:
        return "";
    }
  };

  const handleSaveEdit = (
    currentEditItem: { id: string; text: string; replacement: string },
    tempReplacement: string
  ) => {
    if (!currentEditItem) return;

    const newDocumentData = { ...documentData };

    // Check if this is a new item or editing existing
    if (currentEditItem.id.startsWith("new-")) {
      // Find the paragraph containing the text and add new sensitive item
      newDocumentData.sensitive.push({
        id: currentEditItem.id,
        text: currentEditItem.text,
        replacement: tempReplacement,
      });
      console.log("currentEditItem Text",currentEditItem.text);
      newDocumentData.paragraph = newDocumentData.paragraph.replace(
        currentEditItem.text,
        tempReplacement
      );
    } else {
      // Update existing item
      const sensitiveIndex = newDocumentData.sensitive.findIndex(
        (s) => s.id === currentEditItem.id
      );

      if (sensitiveIndex >= 0) {
        const oldReplacement = newDocumentData.sensitive[sensitiveIndex].replacement;
        newDocumentData.sensitive[sensitiveIndex].replacement =
          tempReplacement;
        newDocumentData.paragraph = newDocumentData.paragraph.replace(
          oldReplacement,
          tempReplacement
        );
      }
    }

    setDocumentData(newDocumentData);
    setHasManualEdits(true);

    toast.success("Edit saved", {
      description: "The changes to anonymized text have been applied.",
    });
  };

  const handleDownload = (type: "anonymized" | "summary") => {
    toast.info(`Downloading ${type} document`, {
      description: "Your file will download shortly.",
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
