package org.example.walletservice.service.logger;

import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.time.Instant;
import java.util.*;


/**
 * The class is designed to record all player actions.
 */
public final class TransactionLog {
	private final Map<String, List<String>> transactionRecords = new TreeMap<>();
	private static TransactionLog instance;

	private TransactionLog() {
	}

	/**
	 * The method returns a single instance of the TransactionLog type.
	 * If the instance has not yet been created, a new instance is created,
	 * otherwise the existing instance is returned
	 *
	 * @return a single instance of type TransactionLog
	 */
	public static TransactionLog getInstance() {
		if (instance == null){
			instance = new TransactionLog();
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
	public void recordTransaction(Operation operation, String username, Status status) {
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
	public List<String> getAllTransactionRecords() {
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
	public List<String> getLogsForPlayer(String username) {
		return transactionRecords.get(username);
	}
}