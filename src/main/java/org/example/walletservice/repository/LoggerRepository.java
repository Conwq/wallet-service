package org.example.walletservice.repository;

import org.example.walletservice.model.entity.Log;

import java.util.List;

/**
 * Logs all player actions
 */
public interface LoggerRepository {

	/**
	 * A method that writes logs to the log.
	 *
	 * @param log     An object of type log that we will write to the database.
	 */
	void recordAction(Log log);

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @return A list of transaction logs for the all players.
	 */
	List<Log> findAllActivityRecords();

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param playerID Player id of the player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<Log> findActivityRecordsForPlayer(int playerID);
}
