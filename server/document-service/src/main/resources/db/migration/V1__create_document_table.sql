-- enable UUIDs
CREATE EXTENSION IF NOT EXISTS "pgcrypto";

-- enums
CREATE TYPE document_status AS ENUM ('Uploaded','Parsed','Error');
-- core table
CREATE TABLE document (
  id            UUID PRIMARY KEY DEFAULT gen_random_uuid(),
  user_id       UUID NOT NULL,
  file_name     TEXT NOT NULL,
  file_url      TEXT NOT NULL,
  status        document_status NOT NULL DEFAULT 'Uploaded',
  upload_date   TIMESTAMPTZ NOT NULL DEFAULT now()
);
-- extracted text in child table
CREATE TABLE document_text (
  document_id   UUID PRIMARY KEY REFERENCES document(id) ON DELETE CASCADE,
  extracted_txt TEXT
);

CREATE INDEX idx_document_status ON document(status);
