package org.example.walletservice.service;

import org.example.walletservice.model.entity.Player;

/**
 * Shows the amount of funds on the account, displays the history of transactions and makes a debit/credit.
 */
public interface TransactionService {

	/**
	 * Gets the balance of a player.
	 *
	 * @param player The player for whom the balance is requested.
	 */
	void displayPlayerBalance(Player player);

	/**
	 * Credits a player's account.
	 *
	 * @param player The player to which the account is credited.
	 */
	void credit(Player player);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param player The player from which funds are debited.
	 */
	void debit(Player player);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param player The player for whom the transaction history is being requested.
	 */
	void displayPlayerTransactionalHistory(Player player);
}
