package org.example.walletservice.controller;

import org.example.walletservice.model.Player;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.ServiceProvider;

/**
 * Controller class to perform player operations.
 */
public final class PlayerController {
	private static PlayerController instance;
	private final PlayerService playerService = ServiceProvider.getInstant().getPlayerService();

	private PlayerController(){
	}

	public static PlayerController getInstance(){
		if(instance == null){
			instance = new PlayerController();
		}
		return instance;
	}

	/**
	 *Player registration
	 */
	public void registrationPlayer(String username, String password){
		playerService.registrationPlayer(username, password);
	}

	/**
	 *Log in player
	 */
	public Player logIn(String username, String password) {
		return playerService.logIn(username, password);
	}

	/**
	 *Player balance display
	 */
	public void showPlayerBalance(Player player){
		playerService.showPlayerBalance(player);
	}

	/**
	 *Increasing player balance
	 */
	public void credit(Player player) {
		playerService.credit(player);
	}

	/**
	 *Withdrawal from a player's account
	 */
	public void debit(Player player){
		playerService.debit(player);
	}

	/**
	 *Display the calling player's entire successful transaction history
	 */
	public void showPlayerTransactionalHistory(Player player) {
		playerService.getPlayerTransactionalHistory(player);
	}

	/**
	 *Show all player logs
	 * @param player The user who is viewing the logs
	 */
	public void showAllLogs(Player player){
		playerService.showAllLogs(player);
	}

	/**
	 * Viewing User Logs.
	 * @param player A user who views the log.
	 * @param inputUsernameForSearch Name of the user whose logs are being viewed.
	 */
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		playerService.showLogsByUsername(player, inputUsernameForSearch);
	}
}
