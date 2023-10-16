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
	public void displayPlayerBalance(Player player) {
		playerAccessService.displayPlayerBalance(player);
	}

	/**
	 * Increasing player balance
	 */
	public void credit(Player player) {
		transactionService.credit(player);
	}

	/**
	 * Withdrawal from a player's account
	 */
	public void debit(Player player) {
		transactionService.debit(player);
	}

	/**
	 * Display the calling player's entire successful transaction history
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
