package org.example.walletservice.controller;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.TransactionService;

/**
 * Controller class to perform player operations.
 */
public final class FrontController {
	private final PlayerAccessService playerAccessService;
	private final TransactionService transactionService;
	private final LoggerService loggerService;

	public FrontController(PlayerAccessService playerAccessService, TransactionService transactionService,
						   LoggerService loggerService) {
		this.playerAccessService = playerAccessService;
		this.transactionService = transactionService;
		this.loggerService = loggerService;
	}

	/**
	 * Player registration
	 *
	 * @param username Registration username
	 * @param password Registration password
	 */
	public void registrationPlayer(String username, String password) {
		playerAccessService.registrationPlayer(username, password);
	}

	/**
	 * Log in player
	 *
	 * @param username Login username
	 * @param password Login password
	 */
	public Player logIn(String username, String password) {
		return playerAccessService.logIn(username, password);
	}

	/**
	 * Player balance display
	 */
	public void displayPlayerBalance(Player player) {
		playerAccessService.displayPlayerBalance(player);
	}

	/**
	 * Credits the specified amount to the player's account.
	 *
	 * @param player            The player to credit the amount.
	 * @param inputPlayerAmount The amount to be credited.
	 * @param transactionToken  The transaction token associated with the credit operation.
	 */
	public void credit(Player player, double inputPlayerAmount, String transactionToken) {
		transactionService.credit(player, inputPlayerAmount, transactionToken);
	}

	/**
	 * Withdraws the specified amount from the player's account.
	 *
	 * @param player            The player from whom the amount is to be debited.
	 * @param inputPlayerAmount The amount to be debited.
	 * @param transactionToken  The transaction token associated with the debit operation.
	 */
	public void debit(Player player, double inputPlayerAmount, String transactionToken) {
		transactionService.debit(player, inputPlayerAmount, transactionToken);
	}

	/**
	 * Display the calling player's entire successful transaction history.
	 *
	 * @param player The player whose transaction history will be shown.
	 */
	public void displayPlayerTransactionalHistory(Player player) {
		transactionService.displayPlayerTransactionalHistory(player);
	}

	/**
	 * Show all player logs
	 *
	 * @param player Player who is viewing the logs
	 */
	public void showAllLogs(Player player) {
		loggerService.showAllLogs(player);
	}

	/**
	 * Viewing User Logs.
	 *
	 * @param player                 Player who views the log.
	 * @param inputUsernameForSearch Name of the player whose logs are being viewed.
	 */
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		loggerService.showLogsByUsername(player, inputUsernameForSearch);
	}
}
