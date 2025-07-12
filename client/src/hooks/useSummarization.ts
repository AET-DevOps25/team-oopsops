import { useState } from 'react';
import { toast } from "sonner";
import { summarize } from '@/services/genaiService';
import { DocumentContent } from "@/types/documentContent";
import { jsPDF } from 'jspdf';


const levelMap = {
  1: "short",
  2: "medium",
  3: "long",
} as const;

export const useSummarization = (documentData: DocumentContent,
  setDocumentData: (updateFn: (prev: DocumentContent) => DocumentContent) => void
) => {
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [summary, setSummary] = useState('');
  const [summarizationLevel, setSummarizationLevel] = useState(2);

  const getSummarizationLevelDescription = (level: number) => {
    switch (level) {
      case 1:
        return "Short - Short summary with key points";
      case 2:
        return "Medium - Medium-length Summary with details";
      case 3:
        return "Long - Long summary with comprehensive details";
      default:
        return "";
    }
  };

  const handleSummarizationLevel = (value: number[]) => {
    setSummarizationLevel(value[0]);
  };

  const handleDownloadSummary = () => {
    if (!documentData?.summary) {
      console.warn('No summary to download.');
      return;
    }

    const doc = new jsPDF();
    const margin = 10;
    const lineHeight = 10;
    const lines = doc.splitTextToSize(summary, 180);

    doc.setFontSize(12);
    doc.text(`Summary for "${documentData.title}"`, margin, margin);
    doc.text(lines, margin, margin + lineHeight);

    doc.save(`${documentData.title}_summary.pdf`);
  };

  const handleGenerateSummary = async () => {
    setIsSummarizing(true);

    toast.info("Generating summary...", {
      description: "Please wait while we create a summary of your document.",
    });
    const response = await summarize(documentData.paragraph, levelMap[summarizationLevel]);
    console.log("Summary response:", response);

    const newSummary = response.responseText;
    setSummary(newSummary);
    setDocumentData(prev => ({
      ...prev,
      summary: newSummary,
    }));
    toast.success("Summary generated", {
      description: "Document summary has been created successfully.",
    });
    setIsSummarizing(false);
  };

  return {
    summarizationLevel,
    isSummarizing,
    summary,
    handleSummarizationLevel,
    handleGenerateSummary,
    getSummarizationLevelDescription,
    handleDownloadSummary
  };
};
