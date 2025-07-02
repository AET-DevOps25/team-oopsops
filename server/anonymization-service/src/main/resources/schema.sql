

CREATE TABLE IF NOT EXISTS anonymization (
  id                   UUID            PRIMARY KEY,
  document_id          UUID            NOT NULL,
  original_text        TEXT            NOT NULL,
  anonymized_text      TEXT            NOT NULL,
  anonymization_level  TEXT            NOT NULL ,
  changed_terms        JSONB            NOT NUll,
  created           TIMESTAMPTZ     NOT NULL DEFAULT now()
);

