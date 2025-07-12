import { useState } from 'react';
import { toast } from '@/hooks/use-toast';

export const useSummarization = (documentSummary: string) => {
  const [isSummarizing, setIsSummarizing] = useState(false);
  const [summary, setSummary] = useState('');

  const handleGenerateSummary = () => {
    console.log('Generating summary for document:', documentSummary);
    setIsSummarizing(true);

    setTimeout(() => {
      setIsSummarizing(false);
      setSummary(documentSummary);
      toast({
        title: 'Summary generated',
        description: 'Document summary has been created successfully.',
      });
    }, 2000);
  };

  return {
    isSummarizing,
    summary,
    handleGenerateSummary,
  };
};
