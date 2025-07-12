import React from 'react';
import { Button } from '@/components/ui/button';
import { Slider } from '@/components/ui/slider';
import { Download } from 'lucide-react';

type SummarizationPanelProps = {
  summarizationLevel: number;
  isSummarizing: boolean;
  summary: string;
  onGenerateSummary: () => void;
  onSummarizationLevelChange: (value: number[]) => void;
  onDownload: (type: 'anonymized' | 'summary') => void;
  getLevelDescription: (level: number) => string;
};

const SummarizationPanel = ({
  summarizationLevel,
  isSummarizing,
  summary,
  onGenerateSummary,
  onSummarizationLevelChange,
  getLevelDescription,
  onDownload,
}: SummarizationPanelProps) => {
  const shouldShowSummarized = summary && summary.length > 0;
  console.log("SummarizationPanel rendered with summary:", summary);
  return (
    <div className="space-y-6">
      <div className="glass-panel p-6">
        <h2 className="text-xl font-semibold mb-4">Summarization Options</h2>
        <div className="mb-8">
          <div className="flex justify-between items-center mb-2">
            <span className="font-medium">Summarization Level</span>
            <span className="text-sm text-muted-foreground">
              {getLevelDescription(summarizationLevel)}
            </span>
          </div>
          <Slider
            defaultValue={[2]}
            max={3}
            min={1}
            step={1}
            value={[summarizationLevel]}
            onValueChange={onSummarizationLevelChange}
          />
          <div className="flex justify-between mt-2 text-xs text-muted-foreground">
            <span>Light</span>
            <span>Medium</span>
            <span>Heavy</span>
          </div>
        </div>
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
          {shouldShowSummarized && (
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
