CREATE TABLE IF NOT EXISTS users (
  id          UUID            PRIMARY KEY,
  username    TEXT            NOT NULL,
  email       TEXT            NOT NULL,
  password_hash VARCHAR(255) NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);
