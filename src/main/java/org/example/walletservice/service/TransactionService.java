package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;

import java.util.List;

/**
 * Shows the amount of funds on the account, displays the history of transactions and makes a debit/credit.
 */
public interface TransactionService {

	/**
	 * Credits a player's account.
	 *
	 * @param player            The player to which the account is credited.
	 * @param inputPlayerAmount The amount entered by the player for a credit transaction.
	 * @param transactionToken  Token for the current transaction.
	 */
	void credit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequest);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param player            The player from which funds are debited.
	 * @param inputPlayerAmount The amount entered by the player for a credit transaction.
	 * @param transactionToken  Token for the current transaction.
	 */
	void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequest);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param player The player for whom the transaction history is being requested.
	 */
	List<String> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto);
}
