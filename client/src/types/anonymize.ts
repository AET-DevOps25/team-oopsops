

export type ChangedTerm = {
  original: string;
  anonymized: string;
};

export type AnonymizationRequestBody = {
  originalText: string;
  anonymizedText: string;
  userId: string;
  level: string;
  changedTerms: ChangedTerm[];
};
