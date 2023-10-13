package org.example.walletservice.service;

import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

public interface PlayerActionLoggerService {

	/**
	 * A method that records the actions of players
	 *
	 * @param operation Player's Operation
	 * @param username  Username of the player who performs the action
	 * @param status    Status of the action
	 */
	void recordAction(Operation operation, String username, Status status);

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @return list all logs.
	 */
	void showAllLogs(String username);

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param username The username of the player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	void showLogsByUsername(String username, String inputUsernameForSearch);
}
