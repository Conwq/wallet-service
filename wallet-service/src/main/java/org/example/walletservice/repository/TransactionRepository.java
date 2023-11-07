package org.example.walletservice.repository;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;

import java.math.BigDecimal;
import java.util.List;

/**
 * Shows the amount of funds on the account, displays the history of transactions and makes a debit/credit.
 */
public interface TransactionRepository {

	/**
	 * Updates the player's balance based on the provided transaction details.
	 *
	 * @param transaction     The Transaction object containing details of the credit or debit operation.
	 * @param newPlayerAmount The new balance of the player after the transaction.
	 */
	void creditOrDebit(Transaction transaction, BigDecimal newPlayerAmount);

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
	 * @param player The player whose transaction history you want to find.
	 * @return A card representing the player's transactional history.
	 */
	List<Transaction> findPlayerTransactionalHistory(Player player);
}
