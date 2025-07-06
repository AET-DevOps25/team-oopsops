export const mockDocument = {
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

export type DocumentContent = typeof mockDocument;
