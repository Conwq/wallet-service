package org.example.walletservice.service;

import org.example.walletservice.model.Player;

/**
 * The PlayerAccessService interface provides methods for managing players in the system.
 */
public interface PlayerAccessService {

	/**
	 * Registers a new player in the system.
	 *
	 * @param username The username for the new player.
	 * @param password The password for the new player.
	 */
	void registrationPlayer(String username, String password);

	/**
	 * Logs in an existing player to the system.
	 *
	 * @param username The username for login.
	 * @param password The password for login.
	 * @return An instance of a Player object that successfully logged on.
	 */
	Player logIn(String username, String password);

}
