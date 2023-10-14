CREATE TABLE wallet_service.roles(role_id SERIAL PRIMARY KEY, role_name VARCHAR);

INSERT INTO wallet_service.roles(role_name) VALUES ('user'), ('admin');