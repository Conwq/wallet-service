package org.example.walletservice.repository;

import org.example.walletservice.model.entity.Player;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * This interface defines what behavior the classes that implement it will have.
 */
public interface PlayerRepository {
	/**
	 * Finds a player by their username.
	 *
	 * @param username The player's username.
	 * @return Optional object containing the player if found or empty if not found.
	 */
	Optional<Player> findPlayer(String username);

	/**
	 * Registers a new player.
	 *
	 * @param player to register.
	 */
	int registrationPayer(Player player);

	/**
	 * Gets the player's balance in the form of a line.
	 *
	 * @param player The player whose balance you want to receive.
	 * @return String representation of the player's balance.
	 */
	Player findPlayerBalance(Player player);
}