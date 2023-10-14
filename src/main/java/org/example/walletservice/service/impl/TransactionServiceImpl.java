package org.example.walletservice.service.impl;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.util.Cleaner;

import java.util.List;
import java.util.Scanner;

public final class TransactionServiceImpl implements TransactionService {
	private final Scanner scanner;
	private final LoggerService loggerService;
	private final Cleaner cleaner;
	private final TransactionRepository transactionRepository;

	public TransactionServiceImpl(Scanner scanner, LoggerService loggerService,
								  Cleaner cleaner, TransactionRepository transactionRepository) {
		this.scanner = scanner;
		this.loggerService = loggerService;
		this.cleaner = cleaner;
		this.transactionRepository = transactionRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerBalance(Player player) {
		double balance = transactionRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
		System.out.printf("\n*Balance -- %s*\n\n", balance);

		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(Player player) {
		System.out.print("Please enter the amount credit: ");

		double amountTransaction;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((amountTransaction = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null) {
			double playerBalance = transactionRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
			double newPlayerBalance = playerBalance + amountTransaction;
			transactionRepository.creditOrDebit(newPlayerBalance, player.getPlayerID(),
					transactionalToken, Operation.CREDIT);
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
	public void debit(Player player) {
		System.out.print("Enter the amount to withdraw funds: ");

		double inputPlayerAmount;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((inputPlayerAmount = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null) {

			double currentAmountOfFundsInAccount = transactionRepository
					.findPlayerBalanceByPlayerID(player.getPlayerID());
			if (currentAmountOfFundsInAccount - inputPlayerAmount < 0.0) {
				System.out.println("\n*{{FAIL}} There are not enough funds in the account!*\n");
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
				return;
			}
			double newPlayerBalance = currentAmountOfFundsInAccount - inputPlayerAmount;
			transactionRepository.creditOrDebit(newPlayerBalance, player.getPlayerID(),
					transactionalToken, Operation.DEBIT);
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

	/**
	 * Checks the user's entered transaction number for uniqueness.
	 *
	 * @return transaction number or {@code null} if it already exists.
	 */
	private String checkingEnteredUserToken() {
		cleaner.cleanBuffer(scanner);
		System.out.print("Please enter transaction number: ");
		String transactionalToken = scanner.nextLine();

		if (transactionRepository.checkTokenExistence(transactionalToken)) {
			return null;
		}
		return transactionalToken;
	}
}
