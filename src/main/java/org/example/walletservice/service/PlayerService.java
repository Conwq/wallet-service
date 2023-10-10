package org.example.walletservice.service;

import org.example.walletservice.model.Player;

/**
 * This interface defines what behavior the classes that implement it will have.
 */
public interface PlayerService {
	void registrationPlayer(String username, String password);
	Player logIn(String username, String password);
	void credit(Player player);
	void debit(Player player);
	void getPlayerTransactionalHistory(Player player);
	void getPlayerBalance(Player player);
	void showAllLogs(Player player);
	void showLogsByUsername(Player player, String inputUsernameForSearch);
}