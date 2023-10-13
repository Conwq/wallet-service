package org.example.walletservice.service.impl;

import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.util.Cleaner;

import java.util.List;
import java.util.Scanner;

public final class TransactionServiceImpl implements TransactionService {
	private final Scanner scanner;
	private final PlayerActionLoggerService playerActionLoggerService;
	private final Cleaner cleaner;
	private final TransactionRepository transactionRepository;

	public TransactionServiceImpl(Scanner scanner, PlayerActionLoggerService playerActionLoggerService,
								  Cleaner cleaner, TransactionRepository transactionRepository) {
		this.scanner = scanner;
		this.playerActionLoggerService = playerActionLoggerService;
		this.cleaner = cleaner;
		this.transactionRepository = transactionRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(String username) {
		System.out.print("Please enter the amount credit: ");

		double amountTransaction;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((amountTransaction = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null) {

			transactionRepository.credit(amountTransaction, username, transactionalToken);

			System.out.println("\n*Credit successfully.*\n");

			playerActionLoggerService.recordAction(Operation.CREDIT, username, Status.SUCCESSFUL);
		} else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			playerActionLoggerService.recordAction(Operation.CREDIT, username, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(String username) {
		System.out.print("Enter the amount to withdraw funds: ");

		double inputPlayerAmount;
		String transactionalToken;
		if (scanner.hasNextDouble() && ((inputPlayerAmount = scanner.nextDouble()) >= 0.0)
				&& (transactionalToken = checkingEnteredUserToken()) != null) {

			double currentAmountOfFundsInAccount = transactionRepository.findPlayerBalanceByUsername(username);

			if (currentAmountOfFundsInAccount - inputPlayerAmount < 0.0) {
				System.out.println("\n*{{FAIL}} There are not enough funds in the account!*\n");

				playerActionLoggerService.recordAction(Operation.DEBIT, username, Status.FAIL);
				return;
			}

			transactionRepository.debit(inputPlayerAmount, username, transactionalToken);
			System.out.println("\n*Debit successfully.*\n");

			playerActionLoggerService.recordAction(Operation.DEBIT, username, Status.SUCCESSFUL);
		} else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			playerActionLoggerService.recordAction(Operation.DEBIT, username, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerTransactionalHistoryByUsername(String username) {
		List<String> playerTransactionalHistory =
				transactionRepository.findPlayerTransactionalHistoryByUsername(username);

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
		playerActionLoggerService.recordAction(Operation.TRANSACTIONAL_HISTORY, username, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerBalance(String username) {
		double balance = transactionRepository.findPlayerBalanceByUsername(username);
		System.out.printf("\n*Balance -- %s*\n\n", balance);

		playerActionLoggerService.recordAction(Operation.VIEW_BALANCE, username, Status.SUCCESSFUL);
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
