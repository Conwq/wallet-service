package org.example.walletservice.service;

import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.dto.PlayerDto;

import java.math.BigDecimal;

/**
 * The PlayerService interface provides methods for managing players in the system.
 */
public interface PlayerService {

	/**
	 * Registers a new player in the system.
	 *
	 * @param username The username for the new player.
	 * @param password The password for the new player.
	 */
	void registrationPlayer(PlayerRequestDto playerRequestDto);

	/**
	 * Logs in an existing player to the system.
	 *
	 * @param username The username for login.
	 * @param password The password for login.
	 * @return An instance of a Player object that successfully logged on.
	 */
	PlayerDto logIn(PlayerRequestDto playerRequestDto);

	/**
	 * Gets the balance of a player.
	 *
	 * @param player The player for whom the balance is requested.
	 */
	BigDecimal getPlayerBalance(PlayerDto playerDto);
}
