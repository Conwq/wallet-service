package org.example.walletservice.service.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.logger.PlayerActivityLogger;
import org.example.walletservice.util.Cleaner;

import java.util.Map;
import java.util.Scanner;

public final class TransactionServiceImpl implements TransactionService {
	private final Scanner scanner;
	private final PlayerActivityLogger playerActivityLogger;
	private final Cleaner cleaner;
	private final TransactionRepository transactionRepository;

	public TransactionServiceImpl(Scanner scanner, PlayerActivityLogger playerActivityLogger,
								  Cleaner cleaner, TransactionRepository transactionRepository){
		this.scanner = scanner;
		this.playerActivityLogger = playerActivityLogger;
		this.cleaner = cleaner;
		this.transactionRepository = transactionRepository;
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

			transactionRepository.credit(amountTransaction, player, transactionalToken);

			System.out.println("\n*Credit successfully.*\n");

			playerActivityLogger.recordAction(Operation.CREDIT, player.getUsername(), Status.SUCCESSFUL);
		}
		else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			playerActivityLogger.recordAction(Operation.CREDIT, player.getUsername(), Status.FAIL);
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
				&& (transactionalToken = checkingEnteredUserToken()) != null){

			if (player.getBalance() - inputPlayerAmount < 0.0){
				System.out.println("\n*{{FAIL}} There are not enough funds in the account!*\n");

				playerActivityLogger.recordAction(Operation.DEBIT, player.getUsername(), Status.FAIL);
				return;
			}

			transactionRepository.debit(inputPlayerAmount, player, transactionalToken);
			System.out.println("\n*Debit successfully.*\n");

			playerActivityLogger.recordAction(Operation.DEBIT, player.getUsername(), Status.SUCCESSFUL);
		}
		else {
			System.out.println("\n*{{FAIL}} A transaction with this number already exists!*\n");
			playerActivityLogger.recordAction(Operation.DEBIT, player.getUsername(), Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerTransactionalHistory(Player player) {
		Map<String, String> playerTransactionalHistory =
				transactionRepository.getPlayerTransactionalHistory(player.getUsername());

		if (playerTransactionalHistory == null){
			System.out.println("\nUNKNOWN ERROR\n");
			return;
		}

		if (playerTransactionalHistory.isEmpty()){
			System.out.println("\n**********************");
			System.out.println("Transactions is empty.");
			System.out.println("**********************\n");
			return;
		}

		for (Map.Entry<String, String> transaction : playerTransactionalHistory.entrySet()) {
			System.out.println(transaction.getValue());
		}
		playerActivityLogger.recordAction(Operation.TRANSACTIONAL_HISTORY, player.getUsername(), Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerBalance(Player player) {
		String balance = transactionRepository.getPlayerBalance(player);
		System.out.printf("\n*Balance -- %s*\n\n", balance);

		playerActivityLogger.recordAction(Operation.VIEW_BALANCE, player.getUsername(), Status.SUCCESSFUL);
	}

	/**
	 * Checks the user's entered transaction number for uniqueness.
	 *
	 * @return transaction number or {@code null} if it already exists.
	 */
	private String checkingEnteredUserToken(){
		cleaner.cleanBuffer(scanner);
		System.out.print("Please enter transaction number: ");
		String transactionalToken = scanner.nextLine();

		if(transactionRepository.checkTokenExistence(transactionalToken)) {
			return null;
		}
		return transactionalToken;
	}
}
