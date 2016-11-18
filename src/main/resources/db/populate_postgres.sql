DELETE FROM users;

INSERT INTO users (id, name, email, password, roles, registereddate, activated) VALUES
  ('TestUser1', 'Синий Гном', 'gnom@email.com', 'gnompass', 1, '2016-11-17 13:00:00', TRUE),
  ('TestUser2', 'Царь всея приложение', 'tsar@email.com', 'tsarpass', 3, '2016-11-16 00:00:01', TRUE),
  ('TestUser3', 'Just an admin', 'admin@email.com', 'godpass', 2, '2016-11-16 13:00:00', TRUE);