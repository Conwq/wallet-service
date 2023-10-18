package org.example.walletservice.service.impl;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.List;

public final class TransactionServiceImpl implements TransactionService {
	private final LoggerService loggerService;
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;

	public TransactionServiceImpl(LoggerService loggerService, TransactionRepository transactionRepository,
								  PlayerRepository playerRepository) {
		this.loggerService = loggerService;
		this.transactionRepository = transactionRepository;
		this.playerRepository = playerRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(Player player, double inputPlayerAmount, String transactionToken) {
		if (inputPlayerAmount >= 0.0 && !transactionRepository.checkTokenExistence(transactionToken)) {
			double playerBalance = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
			double newPlayerBalance = playerBalance + inputPlayerAmount;

			Transaction transaction = createTransaction(transactionToken, Operation.CREDIT,
					inputPlayerAmount, player, newPlayerBalance);

			transactionRepository.creditOrDebit(transaction, newPlayerBalance);
			System.out.println("*Credit successfully.*\n");
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println("*{{FAIL}} A transaction with this number already exists!*\n");
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(Player player, double inputPlayerAmount, String transactionToken) {
		if (inputPlayerAmount >= 0.0 && !transactionRepository.checkTokenExistence(transactionToken)) {
			double playerBalance = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
			if (playerBalance - inputPlayerAmount < 0.0) {
				System.out.println("*{{FAIL}} There are not enough funds in the account!*\n");
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
				return;
			}
			double newPlayerBalance = playerBalance - inputPlayerAmount;

			Transaction transaction = createTransaction(transactionToken, Operation.DEBIT,
					inputPlayerAmount, player, newPlayerBalance);

			transactionRepository.creditOrDebit(transaction, newPlayerBalance);
			System.out.println("*Debit successfully.*\n");
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println("*{{FAIL}} A transaction with this number already exists!*\n");
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerTransactionalHistory(Player player) {
		List<String> playerTransactionalHistory =
				transactionRepository.findPlayerTransactionalHistoryByPlayerID(player.getPlayerID());

		if (playerTransactionalHistory == null) {
			System.out.println("The database is not available at the moment. Try again later.");
			return;
		}

		if (playerTransactionalHistory.isEmpty()) {
			System.out.println("**********************");
			System.out.println("Transactions is empty.");
			System.out.println("**********************\n");
			loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
			return;
		}

		for (String transaction : playerTransactionalHistory) {
			System.out.println(transaction);
		}
		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
	}

	/**
	 * Creates a Transaction object based on the provided parameters.
	 *
	 * @param token             The unique identifier for the transaction.
	 * @param operation         The operation type (e.g., CREDIT, DEBIT).
	 * @param inputPlayerAmount The amount involved in the transaction.
	 * @param player            The Player associated with the transaction.
	 * @param newPlayerBalance  The new balance of the Player after the transaction.
	 * @return A Transaction object representing the transaction details.
	 */
	private Transaction createTransaction(String token, Operation operation, double inputPlayerAmount,
										  Player player, double newPlayerBalance) {
		return Transaction.builder()
				.token(token)
				.operation(Operation.CREDIT.name())
				.amount(inputPlayerAmount)
				.playerID(player.getPlayerID())
				.record(String.format("*****************-%s-*****************\n" +
								"\t-- Transaction number: %s\n" +
								"\t-- Your balance after transaction: %s\n" +
								"******************************************\n",
						operation.name(), token, newPlayerBalance))
				.build();
	}
}
