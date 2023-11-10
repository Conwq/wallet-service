package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;

import java.util.List;

/**
 * Shows the amount of funds on the account, displays the history of transactions and makes a debit/credit.
 */
public interface TransactionService {

	/**
	 * Credits a player's account.
	 *
	 * @param authPlayer         The authenticated player to which the account is credited.
	 * @param transactionRequest The transaction data, including the amount and transaction token.
	 */
	void credit(AuthPlayer authPlayer, TransactionRequestDto transactionRequest)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist;

	/**
	 * Debits funds from a player's account.
	 *
	 * @param authPlayer         The authenticated player from which funds are debited.
	 * @param transactionRequest The transaction data, including the amount and transaction token.
	 */
	void debit(AuthPlayer authPlayer, TransactionRequestDto transactionRequest)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist;

	/**
	 * Gets the transaction history of a player.
	 *
	 * @param authPlayer The authenticated player for whom the transaction history is being requested.
	 * @return A list of strings representing the player's transaction history.
	 */
	List<TransactionResponseDto> getPlayerTransactionalHistory(AuthPlayer authPlayer)
			throws PlayerDoesNotHaveAccessException;
}