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
	 * @param authPlayerDto      The authenticated player to which the account is credited.
	 * @param transactionRequest The transaction data, including the amount and transaction token.
	 */
	void credit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequest);

	/**
	 * Debits funds from a player's account.
	 *
	 * @param authPlayerDto      The authenticated player from which funds are debited.
	 * @param transactionRequest The transaction data, including the amount and transaction token.
	 */
	void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequest);

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param authPlayerDto The authenticated player for whom the transaction history is being requested.
	 * @return A list of strings representing the player's transaction history.
	 */
	List<String> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto);
}