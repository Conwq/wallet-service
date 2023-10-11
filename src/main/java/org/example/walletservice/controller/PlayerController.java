package org.example.walletservice.controller;

import org.example.walletservice.model.Player;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.TransactionService;

/**
 * Controller class to perform player operations.
 */
public final class PlayerController {
	private final PlayerAccessService playerAccessService;
	private final TransactionService transactionService;

	public PlayerController(PlayerAccessService playerAccessService, TransactionService transactionService) {
		this.playerAccessService = playerAccessService;
		this.transactionService = transactionService;
	}

	/**
	 *Player registration
	 */
	public void registrationPlayer(String username, String password){
		playerAccessService.registrationPlayer(username, password);
	}

	/**
	 *Log in player
	 */
	public Player logIn(String username, String password) {
		return playerAccessService.logIn(username, password);
	}

	/**
	 *Player balance display
	 */
	public void displayPlayerBalance(Player player){
		transactionService.displayPlayerBalance(player);
	}

	/**
	 *Increasing player balance
	 */
	public void credit(Player player) {
		transactionService.credit(player);
	}

	/**
	 *Withdrawal from a player's account
	 */
	public void debit(Player player){
		transactionService.debit(player);
	}

	/**
	 *Display the calling player's entire successful transaction history
	 */
	public void displayPlayerTransactionalHistory(Player player) {
		transactionService.displayPlayerTransactionalHistory(player);
	}

	/**
	 *Show all player logs
	 * @param player The user who is viewing the logs
	 */
	public void showAllLogs(Player player){
		playerAccessService.showAllLogs(player);
	}

	/**
	 * Viewing User Logs.
	 * @param player A user who views the log.
	 * @param inputUsernameForSearch Name of the user whose logs are being viewed.
	 */
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		playerAccessService.showLogsByUsername(player, inputUsernameForSearch);
	}
}
