package org.example.walletservice.service;

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
	 * @return list all logs.
	 */
	List<LogResponseDto> getAllLogs() throws PlayerNotLoggedInException, PlayerDoesNotHaveAccessException;

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param inputUsernameForSearch Authorized player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<LogResponseDto> getLogsByUsername(String inputUsernameForSearch)
			throws PlayerNotFoundException, PlayerNotLoggedInException, PlayerDoesNotHaveAccessException;
}
