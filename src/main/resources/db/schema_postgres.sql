DROP TABLE IF EXISTS users;

CREATE TABLE IF NOT EXISTS public.users (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  name VARCHAR NOT NULL,
  email VARCHAR NOT NULL,
  password VARCHAR NOT NULL,
  roles INT NOT NULL,
  registeredDate TIMESTAMP NOT NULL,
  activated BOOL NOT NULL,
  CONSTRAINT email_unique_idx UNIQUE (email)
);

