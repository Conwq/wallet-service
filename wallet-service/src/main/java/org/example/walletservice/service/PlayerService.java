package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * The PlayerService interface provides methods for managing players in the system.
 */
public interface PlayerService {

	/**
	 * Registers a new player in the system.
	 *
	 * @param playerRequestDto The data required for player registration.
	 * @return Player id
	 */
	int registrationPlayer(PlayerRequestDto playerRequestDto)
			throws PlayerAlreadyExistException, PlayerNotLoggedInException, InvalidInputDataException;

	/**
	 * Logs in an existing player to the system.
	 *
	 * @param playerRequestDto The data required for player login, including username and password.
	 * @return Jwt token.
	 */
	String logIn(PlayerRequestDto playerRequestDto)
			throws PlayerNotFoundException, PlayerNotLoggedInException, InvalidInputDataException;

	/**
	 * Gets the balance of a player.
	 *
	 * @param userDetails The authenticated player for whom the balance is requested.
	 * @return The balance of the player as a {@code BigDecimal}.
	 */
	BalanceResponseDto getPlayerBalance(UserDetails userDetails) throws PlayerNotLoggedInException;
}
