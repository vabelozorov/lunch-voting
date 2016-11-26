DELETE FROM phones;
DELETE FROM places;
DELETE FROM users;

INSERT INTO users (id, name, email, password, roles, registereddate, activated) VALUES
  ('VOTER_ID', 'Синий Гном', 'voter@email.com', 'voterpass', 1, '2016-11-17 13:00:00', TRUE),
  ('GOD_ID', 'Царь всея приложение', 'god@email.com', 'godpass', 3, '2016-11-16 00:00:01', TRUE),
  ('ADMIN_ID', 'Just an admin', 'admin@email.com', 'adminpass', 2, '2016-11-16 13:00:00', TRUE);

INSERT INTO places (id, name, address, description, user_id) VALUES
  ('FirstPlaceID', 'First Place', 'First Address', 'First Description', 'ADMIN_ID'),
  ('SecondPlaceID', 'Second Place', 'Second Address', 'Second Description', 'ADMIN_ID'),
  ('ThirdPlaceID', 'Third Place', 'Third Address', 'Third Description', 'GOD_ID'),
  ('FourthPlaceID', 'Fourth Place', 'Fourth Address', 'Fourth Description', 'GOD_ID');

INSERT INTO phones (place_id, phone) VALUES
  ('FirstPlaceID', '0501234567'),
  ('SecondPlaceID', '0502345671'),
  ('SecondPlaceID', '0442345671'),
  ('ThirdPlaceID', '0503456712'),
  ('FourthPlaceID', '0504567123'),
  ('FourthPlaceID', '0934567123'),
  ('FourthPlaceID', '0444567123'),
  ('FourthPlaceID', '0444671235');