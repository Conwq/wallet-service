CREATE TABLE wallet_service.transaction(transaction_id SERIAL PRIMARY KEY, record VARCHAR,
    player_id INT REFERENCES wallet_service.players, token VARCHAR, balance DOUBLE PRECISION DEFAULT 0.0);

INSERT INTO wallet_service.transaction(balance, player_id) VALUES (0.0, 1);