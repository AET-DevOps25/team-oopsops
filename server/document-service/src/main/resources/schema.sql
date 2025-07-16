CREATE TABLE IF NOT EXISTS document (
  id          UUID            PRIMARY KEY,
  user_id     UUID            NOT NULL,
  file_name   TEXT            NOT NULL,
  file_url    TEXT,
  status      TEXT            NOT NULL DEFAULT 'UPLOADED',
  upload_date TIMESTAMPTZ     NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS document_text (
  document_id UUID            PRIMARY KEY,
  text        TEXT            NOT NULL,
  CONSTRAINT fk_document
    FOREIGN KEY(document_id)
    REFERENCES document(id)
    ON DELETE CASCADE
);
