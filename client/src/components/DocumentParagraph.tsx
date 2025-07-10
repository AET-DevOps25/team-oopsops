import React from 'react';

type SensitiveItem = {
  id: string;
  text: string;
  replacement: string;
};

type ParagraphData = {
  paragraph: string;
  sensitive: SensitiveItem[];
};

type DocumentParagraphProps = {
  paragraph: ParagraphData;
  shouldShowAnonymized: boolean;
  onTextSelection: () => void;
  onEditItem: (item: SensitiveItem) => void;
};

const DocumentParagraph = ({
  paragraph,
  shouldShowAnonymized,
  onTextSelection,
  onEditItem,
}: DocumentParagraphProps) => {
  if (!shouldShowAnonymized) {
    return (
      <p className="mb-4" onMouseUp={onTextSelection}>
        {paragraph.paragraph}
      </p>
    );
  }

  let segments = [];
  let lastIndex = 0;

  // Sort by position in text to process in order
  const sortedSensitive = [...paragraph.sensitive].sort(
    (a, b) =>
      paragraph.paragraph.indexOf(a.text) - paragraph.paragraph.indexOf(b.text)
  );

  for (const item of sortedSensitive) {
    const index = paragraph.paragraph.indexOf(item.replacement, lastIndex);
    if (index > -1) {
      // Add text before sensitive info
      if (index > lastIndex) {
        segments.push(
          <span key={`text-${lastIndex}-${index}`} onMouseUp={onTextSelection}>
            {paragraph.paragraph.substring(lastIndex, index)}
          </span>
        );
      }

      segments.push(
        <span
          key={`sensitive-${index}`}
          className="anonymized-pair group cursor-pointer relative"
          onClick={() => onEditItem(item)}
        >
          <span className="text-sensitive">{item.text}</span>
          <span className="text-anonymized ml-1">{item.replacement}</span>
          <span className="absolute -top-8 left-1/2 transform -translate-x-1/2 bg-black text-white text-xs px-2 py-1 rounded opacity-0 group-hover:opacity-100 transition-opacity z-50 whitespace-nowrap pointer-events-none">
            Click to edit • Original → Replacement
          </span>
        </span>
      );

      lastIndex = index + item.replacement.length;
    }
  }

  // Add remaining text after the last sensitive info
  if (lastIndex < paragraph.paragraph.length) {
    segments.push(
      <span key={`text-${lastIndex}-end`} onMouseUp={onTextSelection}>
        {paragraph.paragraph.substring(lastIndex)}
      </span>
    );
  }

  return <p className="mb-4">{segments}</p>;
};

export default DocumentParagraph;
