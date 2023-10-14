CREATE TABLE wallet_service.players (player_id SERIAL PRIMARY KEY, username VARCHAR, password VARCHAR,
role_id INT REFERENCES wallet_service.roles);

INSERT INTO wallet_service.players(username, password, role_id) VALUES ('admin', 'admin', 2);