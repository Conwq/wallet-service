package org.example.walletservice.service;

public interface TransactionService {
	/**
	 * Credits a player's account.
	 *
	 * @param username The username of the player object to which the account is credited.
	 */
	void credit(String username);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param username The username of the player from which funds are debited.
	 */
	void debit(String username);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param username The username of the player for whom the transaction history is being requested.
	 */
	void displayPlayerTransactionalHistoryByUsername(String username);

	/**
	 * Gets the balance of a player.
	 *
	 * @param username The username of the player object for which the balance is requested.
	 */
	void displayPlayerBalance(String username);
}
