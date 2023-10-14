package org.example.walletservice.repository;

import java.util.List;

/**
 * Logs all player actions
 */
public interface LoggerRepository {
	/**
	 * A method that writes logs to the log.
	 *
	 * @param playerID     ID of the player who performed the action.
	 * @param playerAction Actions performed by the player.
	 */
	void recordAction(int playerID, String playerAction);

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @return list all logs.
	 */
	List<String> findAllActivityRecords();

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param playerID Player id of the player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<String> findActivityRecordsForPlayer(int playerID);
}
