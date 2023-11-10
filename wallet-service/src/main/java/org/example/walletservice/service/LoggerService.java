package org.example.walletservice.service;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;

import java.util.List;

/**
 * Logs all player actions
 */
public interface LoggerService {

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @param authPlayer An authorized player who will view the logs.
	 * @return list all logs.
	 */
	List<LogResponseDto> getAllLogs(AuthPlayer authPlayer)
			throws PlayerNotLoggedInException, PlayerDoesNotHaveAccessException;

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param authPlayer             An authorized player who will view the logs.
	 * @param inputUsernameForSearch Authorized player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<LogResponseDto> getLogsByUsername(AuthPlayer authPlayer, String inputUsernameForSearch)
			throws PlayerNotFoundException, PlayerNotLoggedInException, PlayerDoesNotHaveAccessException;
}
