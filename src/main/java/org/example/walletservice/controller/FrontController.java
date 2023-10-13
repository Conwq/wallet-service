package org.example.walletservice.controller;

import org.example.walletservice.model.Player;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.example.walletservice.service.TransactionService;

/**
 * Controller class to perform player operations.
 */
public final class FrontController {
	private final PlayerAccessService playerAccessService;
	private final TransactionService transactionService;
	private final PlayerActionLoggerService playerActionLoggerService;

	public FrontController(PlayerAccessService playerAccessService, TransactionService transactionService, PlayerActionLoggerService playerActionLoggerService) {
		this.playerAccessService = playerAccessService;
		this.transactionService = transactionService;
		this.playerActionLoggerService = playerActionLoggerService;
	}

	/**
	 * Player registration
	 */
	public void registrationPlayer(String username, String password) {
		playerAccessService.registrationPlayer(username, password);
	}

	/**
	 * Log in player
	 */
	public Player logIn(String username, String password) {
		return playerAccessService.logIn(username, password);
	}

	/**
	 * Player balance display
	 */
	public void displayPlayerBalance(String username) {
		transactionService.displayPlayerBalance(username);
	}

	/**
	 * Increasing player balance
	 */
	public void credit(String username) {
		transactionService.credit(username);
	}

	/**
	 * Withdrawal from a player's account
	 */
	public void debit(String username) {
		transactionService.debit(username);
	}

	/**
	 * Display the calling player's entire successful transaction history
	 */
	public void displayPlayerTransactionalHistoryByUsername(String username) {
		transactionService.displayPlayerTransactionalHistoryByUsername(username);
	}

	/**
	 * Show all player logs
	 *
	 * @param username Username of the player who is viewing the logs
	 */
	public void showAllLogs(String username) {
		playerActionLoggerService.showAllLogs(username);
	}

	/**
	 * Viewing User Logs.
	 *
	 * @param username               Username of the player who views the log.
	 * @param inputUsernameForSearch Name of the player whose logs are being viewed.
	 */
	public void showLogsByUsername(String username, String inputUsernameForSearch) {
		playerActionLoggerService.showLogsByUsername(username, inputUsernameForSearch);
	}
}
