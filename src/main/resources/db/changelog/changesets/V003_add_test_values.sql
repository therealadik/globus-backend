INSERT INTO users (username, password, role) VALUES
                                                 ('user1', '{noop}password123', 'USER'),
                                                 ('user2', '{noop}securepass', 'USER');
INSERT INTO categories (name) VALUES
                                  ('Зарплата'),
                                  ('Продукты'),
                                  ('Коммунальные услуги'),
                                  ('Развлечения'),
                                  ('Аренда'),
                                  ('Перевод');

INSERT INTO banks (name) VALUES
                             ('Сбербанк'),
                             ('ВТБ'),
                             ('Альфа-Банк'),
                             ('Тинькофф Банк'),
                             ('Газпромбанк');