package org.example.walletservice.repository.impl;

import org.example.walletservice.database.CustomDatabase;
import org.example.walletservice.model.Player;
import org.example.walletservice.repository.PlayerRepository;

import java.util.Optional;

/**
 * Реализация интерфейса {@link PlayerRepository}, управление данными игроков и транзакциями.
 */
public final class PlayerRepositoryImpl implements PlayerRepository {
	private final CustomDatabase customDatabase;

	public PlayerRepositoryImpl(CustomDatabase customDatabase) {
		this.customDatabase = customDatabase;
	}

	/**
	 * Finds a player by their username.
	 *
	 * @param username The player's username.
	 * @return Optional object containing the player if found or empty if not found.
	 */
	@Override
	public Optional<Player> findPlayer(String username){
		return customDatabase.getPlayer(username);
	}

	/**
	 * Registers a new player.
	 *
	 * @param player to register.
	 */
	@Override
	public void registrationPayer(Player player) {
		customDatabase.savePlayer(player.getUsername(), player);
	}
}