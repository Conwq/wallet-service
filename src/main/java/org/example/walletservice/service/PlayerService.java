package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;

import java.math.BigDecimal;
import java.util.Optional;

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
	 * Retrieves an {@link Optional} containing a {@link Player} based on the provided username.
	 *
	 * @param username The username of the player to be retrieved.
	 * @return An {@link Optional} containing the found {@link Player} if present, otherwise an empty {@link Optional}.
	 * @apiNote This method is typically used for retrieving a player by their unique username.
	 * @see Player
	 * @see Optional
	 */
	Optional<Player> findByUsername(String username);

	/**
	 * Gets the balance of a player.
	 *
	 * @param authPlayerDto The authenticated player for whom the balance is requested.
	 * @return The balance of the player as a {@code BigDecimal}.
	 */
	BigDecimal getPlayerBalance(AuthPlayerDto authPlayerDto) throws PlayerNotLoggedInException;
}
