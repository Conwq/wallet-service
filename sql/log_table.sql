CREATE TABLE wallet_service.log (log_id SERIAL PRIMARY KEY, log VARCHAR,
 player_id INT REFERENCES wallet_service.players);