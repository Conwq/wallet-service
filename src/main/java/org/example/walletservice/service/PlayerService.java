package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;

import java.math.BigDecimal;

/**
 * The PlayerService interface provides methods for managing players in the system.
 */
public interface PlayerService {

	/**
	 * Registers a new player in the system.
	 *
	 * @param playerRequestDto The data required for player registration.
	 */
	void registrationPlayer(PlayerRequestDto playerRequestDto);

	/**
	 * Logs in an existing player to the system.
	 *
	 * @param playerRequestDto The data required for player login, including username and password.
	 * @return An instance of {@code AuthPlayerDto} representing the player who successfully logged in.
	 */
	AuthPlayerDto logIn(PlayerRequestDto playerRequestDto);

	/**
	 * Gets the balance of a player.
	 *
	 * @param authPlayerDto The authenticated player for whom the balance is requested.
	 * @return The balance of the player as a {@code BigDecimal}.
	 */
	BigDecimal getPlayerBalance(AuthPlayerDto authPlayerDto);
}
