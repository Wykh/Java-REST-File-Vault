INSERT INTO role_entity (id, name) VALUES ('d5c940bf-70e0-4a6d-a42e-065788d92626', 'ADMIN');
INSERT INTO role_entity (id, name) VALUES ('8b0b81dd-acf8-4809-86c2-7df85700c998', 'MANAGER');
INSERT INTO role_entity (id, name) VALUES ('3441a82c-20ae-4b6b-a23a-88ee3eaf5037', 'USER');

INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('admin', 'admin', 'd5c940bf-70e0-4a6d-a42e-065788d92626', FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('atrem', '123', '8b0b81dd-acf8-4809-86c2-7df85700c998', FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('irina', '123', '3441a82c-20ae-4b6b-a23a-88ee3eaf5037', FALSE);
INSERT INTO user_entity (name, password, role_id, is_blocked) VALUES ('maxim', '123', '3441a82c-20ae-4b6b-a23a-88ee3eaf5037', FALSE);
