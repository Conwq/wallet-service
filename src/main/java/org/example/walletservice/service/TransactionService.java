package org.example.walletservice.service;

import org.example.walletservice.model.Player;

public interface TransactionService {
	/**
	 * Credits a player's account.
	 *
	 * @param player Player object to which the account is credited.
	 */
	void credit(Player player);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param player Player object from which funds are debited.
	 */
	void debit(Player player);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param player Player object for which the transaction history is requested.
	 */
	void displayPlayerTransactionalHistory(Player player);

	/**
	 * Gets the balance of a player.
	 *
	 * @param player Player object for which the balance is requested.
	 */
	void displayPlayerBalance(Player player);
}
