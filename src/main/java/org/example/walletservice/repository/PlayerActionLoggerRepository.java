package org.example.walletservice.repository;

import java.util.List;
import java.util.Map;

public interface PlayerActionLoggerRepository {
	void recordAction(String username, List<String> playersActionLogg);

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @return list all logs.
	 */
	Map<String, List<String>> findAllActivityRecords();

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param username The username of the player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<String> findActivityRecordsForPlayer(String username);
}
