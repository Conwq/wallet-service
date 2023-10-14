package org.example.walletservice.repository;

import org.example.walletservice.service.enums.Operation;

import java.util.List;

/**
 * Shows the amount of funds on the account, displays the history of transactions and makes a debit/credit.
 */
public interface TransactionRepository {
	/**
	 * Gets the player's balance in the form of a line.
	 *
	 * @param playerID The ID of the player whose balance you want to receive.
	 * @return String representation of the player's balance.
	 */
	double findPlayerBalanceByPlayerID(int playerID);

	/**
	 * Carries out the operation of replenishment of the player's balance.
	 *
	 * @param newPlayerBalance   New player balance.
	 * @param playerID           The ID of the player from whom the deposit is being made.
	 * @param transactionalToken A unique identifier for the transaction.
	 * @param operation          The operation that the player is performing
	 */
	void creditOrDebit(double newPlayerBalance, int playerID, String transactionalToken, Operation operation);

	/**
	 * Checks for the presence of a unique transaction ID.
	 *
	 * @param transactionalToken A unique identifier for the transaction.
	 * @return {@code true} if the transaction ID exists, otherwise {@code false}.
	 */
	boolean checkTokenExistence(String transactionalToken);

	/**
	 * Retrieves the player's transaction history.
	 *
	 * @param playerID The ID of the player.
	 * @return A card representing the player's transactional history.
	 */
	List<String> findPlayerTransactionalHistoryByPlayerID(int playerID);
}
