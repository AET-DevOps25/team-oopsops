

export type ChangedTerm = {
  original: string;
  anonymized: string;
};

export type AnonymizationRequestBody = {
  originalText: string;
  anonymizedText: string;
  level: string;
  changedTerms: ChangedTerm[];
};
