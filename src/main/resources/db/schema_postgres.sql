DROP TABLE IF EXISTS voting_config;
DROP TABLE IF EXISTS phones;
DROP TABLE IF EXISTS dishes;
DROP TABLE IF EXISTS votes;
DROP TABLE IF EXISTS menus;
DROP TABLE IF EXISTS poll_items;
DROP TABLE IF EXISTS places;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS polls;

CREATE TABLE IF NOT EXISTS polls (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  start_time TIMESTAMP NOT NULL,
  end_time TIMESTAMP NOT NULL,
  change_time TIMESTAMP NOT NULL
);

CREATE TABLE IF NOT EXISTS users (
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

CREATE TABLE IF NOT EXISTS places (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  name VARCHAR NOT NULL,
  address VARCHAR,
  description VARCHAR,
  user_id VARCHAR(36) NOT NULL,
  CONSTRAINT name_user_id_unique UNIQUE (name, user_id),
  FOREIGN KEY (user_id) REFERENCES users(id)
);


CREATE TABLE IF NOT EXISTS poll_items (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  poll_id VARCHAR(36) NOT NULL ,
  position INT NOT NULL,
  item_id VARCHAR(36) NOT NULL ,
  FOREIGN KEY (poll_id) REFERENCES polls(id),
  FOREIGN KEY (item_id) REFERENCES places(id)
);

CREATE TABLE IF NOT EXISTS menus (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  effective_date TIMESTAMP NOT NULL,
  place_id VARCHAR(36) NOT NULL,
  FOREIGN KEY (place_id) REFERENCES places(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS votes (
  id VARCHAR(36) NOT NULL PRIMARY KEY,
  version INT NOT NULL DEFAULT 0,
  user_id VARCHAR(36) NOT NULL,
  poll_id VARCHAR(36) NOT NULL,
  item_id VARCHAR(36) NOT NULL,
  voteTime TIMESTAMP NOT NULL,
  FOREIGN KEY (user_id) REFERENCES users(id),
  FOREIGN KEY (poll_id) REFERENCES polls(id),
  FOREIGN KEY (item_id) REFERENCES places(id)
);

CREATE TABLE IF NOT EXISTS dishes (
  menu_id VARCHAR(36) NOT NULL,
  name VARCHAR NOT NULL,
  price FLOAT NOT NULL,
  position INT NOT NULL,
  CONSTRAINT entry_unique UNIQUE (menu_id, name, price),
  FOREIGN KEY (menu_id) REFERENCES menus(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS phones (
  place_id VARCHAR(36) NOT NULL,
  phone VARCHAR NOT NULL,
  PRIMARY KEY (place_id, phone)
);


CREATE TABLE IF NOT EXISTS voting_config (
  poll_start_time TIME NOT NULL DEFAULT '09:00',
  poll_end_time TIME NOT NULL DEFAULT '12:00'
)


