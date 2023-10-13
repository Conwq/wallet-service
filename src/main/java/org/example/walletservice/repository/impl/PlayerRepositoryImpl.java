package org.example.walletservice.repository.impl;

import org.example.walletservice.database.PlayerDatabase;
import org.example.walletservice.database.TransactionDatabase;
import org.example.walletservice.model.Player;
import org.example.walletservice.repository.PlayerRepository;

import java.util.Optional;

/**
 * Реализация интерфейса {@link PlayerRepository}, управление данными игроков и транзакциями.
 */
public final class PlayerRepositoryImpl implements PlayerRepository {
	private final PlayerDatabase playerDatabase;
	private final TransactionDatabase transactionDatabase;

	public PlayerRepositoryImpl(PlayerDatabase playerDatabase, TransactionDatabase transactionDatabase) {
		this.playerDatabase = playerDatabase;
		this.transactionDatabase = transactionDatabase;
	}

	/**
	 * Finds a player by their username.
	 *
	 * @param username The player's username.
	 * @return Optional object containing the player if found or empty if not found.
	 */
	@Override
	public Optional<Player> findPlayer(String username) {
		return playerDatabase.findPlayerByUsername(username);
	}

	/**
	 * Registers a new player.
	 *
	 * @param player to register.
	 */
	@Override
	public void registrationPayer(Player player) {
		playerDatabase.savePlayer(player.getUsername(), player);
		transactionDatabase.savePlayersNewAmountFunds(player.getUsername(), 0.0);
	}
}