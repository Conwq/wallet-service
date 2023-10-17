package org.example.walletservice.service.impl;

import org.example.walletservice.model.entity.Player;
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

	public TransactionServiceImpl(LoggerService loggerService,
								  TransactionRepository transactionRepository,
								  PlayerRepository playerRepository
	) {
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
			transactionRepository.creditOrDebit(newPlayerBalance, player.getPlayerID(), transactionToken,
					Operation.CREDIT);
			System.out.println("\n*Credit successfully.*\n");
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
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
				System.out.println("\n*{{FAIL}} There are not enough funds in the account!*\n");
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
				return;
			}
			double newPlayerBalance = playerBalance - inputPlayerAmount;
			transactionRepository.creditOrDebit(
					newPlayerBalance,
					player.getPlayerID(),
					transactionToken,
					Operation.DEBIT
			);
			System.out.println("\n*Debit successfully.*\n");
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
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
			System.out.println("\nUNKNOWN ERROR\n");
			return;
		}

		if (playerTransactionalHistory.isEmpty()) {
			System.out.println("\n**********************");
			System.out.println("Transactions is empty.");
			System.out.println("**********************\n");
			return;
		}

		for (String transaction : playerTransactionalHistory) {
			System.out.println(transaction);
		}
		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
	}
}
