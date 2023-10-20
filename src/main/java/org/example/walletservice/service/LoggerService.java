package org.example.walletservice.service;

import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.dto.PlayerDto;
import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.List;

/**
 * Logs all player actions
 */
public interface LoggerService {

	/**
	 * A method that records the actions of players
	 *
	 * @param operation Player's Operation
	 * @param player    Username of the player who performs the action
	 * @param status    Status of the action
	 */
	void recordActionInLog(Operation operation, Player player, Status status);

	/**
	 * Using this method we get a list of all player transactions.
	 *
	 * @return list all logs.
	 */
	List<LogResponseDto> getAllLogs(PlayerDto playerDto);

	/**
	 * Retrieves the transaction logs for a specific player.
	 *
	 * @param player Player whose logs are to be retrieved.
	 * @return A list of transaction logs for the specified player, or null if the player's logs are not found.
	 */
	List<LogResponseDto> showLogsByUsername(PlayerDto playerDto, String inputUsernameForSearch);
}
