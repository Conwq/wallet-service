package org.example.walletservice.repository.impl;

import org.example.walletservice.database.TransactionDatabase;
import org.example.walletservice.repository.TransactionRepository;

import java.util.List;

public final class TransactionRepositoryImpl implements TransactionRepository {
	private final TransactionDatabase transactionDatabase;

	public TransactionRepositoryImpl(TransactionDatabase transactionDatabase) {
		this.transactionDatabase = transactionDatabase;
	}

	/**
	 * Checks for the presence of a unique transaction ID.
	 *
	 * @param transactionalToken A unique identifier for the transaction.
	 * @return {@code true} if the transaction ID exists, otherwise {@code false}.
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		return transactionDatabase.containTransactionToken(transactionalToken);
	}

	/**
	 * Carries out the operation of replenishment of the player's balance.
	 *
	 * @param inputPlayerAmount  Deposit transaction amount.
	 * @param username           The username of the player from whom the deposit is being made.
	 * @param transactionalToken A unique identifier for the transaction.
	 */
	@Override
	public void credit(double inputPlayerAmount, String username, String transactionalToken) {
		double playerBalance = transactionDatabase.findPlayerBalanceByUsername(username);
		transactionDatabase.savePlayersNewAmountFunds(username, playerBalance + inputPlayerAmount);

		saveTransactionToken(transactionalToken);

		recordTransactionInPlayerHistory(username, "CREDIT", transactionalToken, inputPlayerAmount);
	}

	/**
	 * Performs a debiting operation from the player's balance.
	 *
	 * @param inputPlayerAmount  Amount of the debited operation.
	 * @param username           The username of the player from whom the charge is being made.
	 * @param transactionalToken A unique identifier for the transaction.
	 */
	@Override
	public void debit(double inputPlayerAmount, String username, String transactionalToken) {
		double playerBalance = transactionDatabase.findPlayerBalanceByUsername(username);
		transactionDatabase.savePlayersNewAmountFunds(username, playerBalance - inputPlayerAmount);

		saveTransactionToken(transactionalToken);

		recordTransactionInPlayerHistory(username, "DEBIT", transactionalToken, inputPlayerAmount);
	}

	/**
	 * Retrieves the player's transaction history.
	 *
	 * @param username The player's username.
	 * @return A card representing the player's transactional history.
	 */
	@Override
	public List<String> findPlayerTransactionalHistoryByUsername(String username) {
		return transactionDatabase.findPlayersTransactionHistoryByUsername(username);
	}

	/**
	 * Gets the player's balance in the form of a line.
	 *
	 * @param username The username of the player whose balance you want to get.
	 * @return String representation of the player's balance.
	 */
	@Override
	public double findPlayerBalanceByUsername(String username) {
		return transactionDatabase.findPlayerBalanceByUsername(username);
	}

	private void saveTransactionToken(String transactionToken) {
		transactionDatabase.saveTransactionToken(transactionToken);
	}

	/**
	 * Records the player's completed transaction in their history.
	 *
	 * @param username           The username of the player making the transaction
	 * @param operation          Operation type
	 * @param transactionalToken Unique Transaction Token
	 * @param inputPlayerAmount  Amount entered by the user
	 */
	private void recordTransactionInPlayerHistory(String username, String operation, String transactionalToken,
												  double inputPlayerAmount) {
		List<String> playerTransactionHistory = transactionDatabase
				.findPlayersTransactionHistoryByUsername(username);

		playerTransactionHistory.add(String.format("*****-%s-*****\n" +
						"\t-- Transaction number: %s\n" +
						"\t-- Amount: %s\n" +
						"\t-- Your balance after transaction: %s\n" +
						"******************************************\n",
				operation, transactionalToken, inputPlayerAmount,
				transactionDatabase.findPlayerBalanceByUsername(username)));

		transactionDatabase.savePlayersTransactionHistory(username, playerTransactionHistory);
	}
}