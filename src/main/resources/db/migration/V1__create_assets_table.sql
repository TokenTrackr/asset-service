CREATE EXTENSION IF NOT EXISTS "pgcrypto";

CREATE TABLE IF NOT EXISTS assets (
                                      id UUID PRIMARY KEY DEFAULT gen_random_uuid(),
                                      crypto_id VARCHAR(255) NOT NULL,
                                      user_id VARCHAR(255) NOT NULL,
                                      quantity NUMERIC(38, 8) NOT NULL DEFAULT 0
);
