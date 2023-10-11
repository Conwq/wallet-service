package org.example.walletservice.service.logger;

import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;


/**
 * The class is designed to record all player actions.
 */
public final class PlayerActivityLogger {
	private final Map<String, List<String>> transactionRecords = new TreeMap<>();
	private static PlayerActivityLogger instance;

	private PlayerActivityLogger() {
	}

	/**
	 * The method returns a single instance of the PlayerActivityLogger type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type PlayerActivityLogger
	 */
	public static PlayerActivityLogger getInstance() {
		if (instance == null){
			instance = new PlayerActivityLogger();
		}
		return instance;
	}

	/**
	 * Method that records the player's action.
	 *
	 * @param operation - operation performed by the user
	 * @param username - the name of the user who performed this operation
	 * @param status - operation status SUCCESSFUL/FAIL
	 */
	public void recordAction(Operation operation, String username, Status status) {
		List<String>transactionalRecordsPlayer = transactionRecords.get(username);
		if (transactionalRecordsPlayer == null){
			transactionalRecordsPlayer = new ArrayList<>();
		}

		transactionalRecordsPlayer.add(String.format("--Operation: %s; \t--User: %s; \t--Data: %s; \t--Status: %s.",
				operation.toString(), username, Instant.now(), status.toString()));

		transactionRecords.put(username, transactionalRecordsPlayer);
	}

	/**
	 * Using this method we get a list of all player transactions.
	 * @return list all logs.
	 */
	public List<String> getAllActivityRecords() {
		List<String> allTransactionRecordsForAllPlayers = new ArrayList<>();
		for (Map.Entry<String, List<String>> recordsList : transactionRecords.entrySet()){
			allTransactionRecordsForAllPlayers.addAll(recordsList.getValue());
		}
		return allTransactionRecordsForAllPlayers;
	}

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param username The username of the player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	public List<String> getActivityRecordsForPlayer(String username) {
		return transactionRecords.get(username);
	}
}