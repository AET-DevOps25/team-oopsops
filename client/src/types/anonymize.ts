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

export type AnonymizationDto = {
  id:                   string
  created:              string
  documentId:           string
  userId:               string
  originalText:         string
  anonymizedText:       string
  anonymization_level:  string
  changedTerms:         ChangedTerm[]
}