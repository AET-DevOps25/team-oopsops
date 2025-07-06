import React from 'react';
import { Button } from '@/components/ui/button';
import { Download } from 'lucide-react';

type SummarizationPanelProps = {
  isSummarizing: boolean;
  summary: string;
  onGenerateSummary: () => void;
  onDownload: (type: 'anonymized' | 'summary') => void;
};

const SummarizationPanel = ({
  isSummarizing,
  summary,
  onGenerateSummary,
  onDownload,
}: SummarizationPanelProps) => {
  return (
    <div className="space-y-6">
      <div className="glass-panel p-6">
        <h2 className="text-xl font-semibold mb-4">Summarization Options</h2>
        <Button
          onClick={onGenerateSummary}
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
              onClick={() => onDownload('summary')}
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
                  Click "Generate Summary" to create a concise summary of this
                  document.
                </p>
              )}
            </div>
          )}
        </div>
      </div>
    </div>
  );
};

export default SummarizationPanel;
