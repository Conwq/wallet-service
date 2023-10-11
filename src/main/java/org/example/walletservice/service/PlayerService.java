package org.example.walletservice.service;

import org.example.walletservice.model.Player;

/**
 * The PlayerService interface provides methods for managing players in the system.
 */
public interface PlayerService {

	/**
	 * Registers a new player in the system.
	 *
	 * @param username The username for the new player.
	 * @param password The password for the new player.
	 */
	void registrationPlayer(String username, String password);

	/**
	 * Logs in an existing player to the system.
	 *
	 * @param username The username for login.
	 * @param password The password for login.
	 * @return An instance of a Player object that successfully logged on.
	 */
	Player logIn(String username, String password);

	/**
	 * Credits a player's account.
	 *
	 * @param player Player object to which the account is credited.
	 */
	void credit(Player player);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param player Player object from which funds are debited.
	 */
	void debit(Player player);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param player Player object for which the transaction history is requested.
	 */
	void getPlayerTransactionalHistory(Player player);

	/**
	 * Gets the balance of a player.
	 *
	 * @param player Player object for which the balance is requested.
	 */
	void showPlayerBalance(Player player);

	/**
	 * Shows all events in the log for a specific player.
	 *
	 * @param player  Player object for which the balance is displayed.
	 */
	void showAllLogs(Player player);

	/**
	 * Shows events in the log for a specific player using a specified username.
	 *
	 * @param player An instance of the Player object for which events are searched.
	 * @param inputUsernameForSearch The username used to search for events in the log.
	 */
	void showLogsByUsername(Player player, String inputUsernameForSearch);
}
