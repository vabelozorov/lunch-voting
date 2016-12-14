DELETE FROM voting_config;
DELETE FROM dishes;
DELETE FROM votes;
DELETE FROM menus;
DELETE FROM poll_items;
DELETE FROM places;
DELETE FROM users;
DELETE FROM polls;


INSERT INTO users (id, name, email, password, roles, registereddate, activated) VALUES
  ('VOTER_ID', 'Синий Гном', 'voter@email.com', 'voterpass', 1, '2016-11-17 13:00:00', TRUE),
  ('GOD_ID', 'Царь всея приложение', 'god@email.com', 'godpass', 3, '2016-11-16 00:00:01', TRUE),
  ('ADMIN_ID', 'Just an admin', 'admin@email.com', 'adminpass', 2, '2016-11-16 13:00:00', TRUE);
--
INSERT INTO places (id, name, address, description, user_id, phones) VALUES
  ('FirstPlaceID', 'First Place', 'First Address', 'First Description', 'ADMIN_ID', '0501234567'),
  ('SecondPlaceID', 'Second Place', 'Second Address', 'Second Description', 'ADMIN_ID', '0502345671,0442345671'),
  ('ThirdPlaceID', 'Third Place', 'Third Address', 'Third Description', 'GOD_ID', '0503456712'),
  ('FourthPlaceID', 'Fourth Place', 'Fourth Address', 'Fourth Description', 'GOD_ID', '0504567123,0934567123,0444567123,0444671235');

-- INSERT INTO menus (id, effective_date, place_id) VALUES
--   ('Menu_1_ID', '2016-11-28', 'FourthPlaceID'),
--   ('Menu_2_ID', '2016-11-29', 'FourthPlaceID'),
--   ('Menu_3_ID', '2016-11-30', 'FourthPlaceID'),
--   ('Menu_4_ID', '2016-12-01', 'FourthPlaceID'),
--   ('Menu_5_ID', '2016-12-01', 'ThirdPlaceID'),
--   ('Menu_6_ID', '2016-12-01', 'ThirdPlaceID');
--
-- INSERT INTO dishes (menu_id, name, price) VALUES
--   ('Menu_1_ID', 'Fish', '11.11'),
--   ('Menu_1_ID', 'Soup', '12.12'),
--   ('Menu_1_ID', 'Apple Juice', '13.13'),
--   ('Menu_2_ID', 'Potato', '21.12'),
--   ('Menu_3_ID', 'Tomato', '31.13'),
--   ('Menu_4_ID', 'Ice cubes', '41.00'),
--   ('Menu_5_ID', 'Marshmallow', '51.00'),
--   ('Menu_6_ID', 'Green Grass', '61.00');