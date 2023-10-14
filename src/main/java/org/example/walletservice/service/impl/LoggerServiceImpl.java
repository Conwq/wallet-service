package org.example.walletservice.service.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.List;
import java.util.Optional;

public class LoggerServiceImpl implements LoggerService {
	private final LoggerRepository loggerRepository;
	private final PlayerRepository playerRepository;

	public LoggerServiceImpl(LoggerRepository loggerRepository, PlayerRepository playerRepository) {
		this.loggerRepository = loggerRepository;
		this.playerRepository = playerRepository;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordActionInLog(Operation operation, Player player, Status status) {
		loggerRepository.recordAction(player.getPlayerID(),
				String.format("--Operation: %s; \t--User: %s; \t--Status: %s.",
						operation.toString(), player.getUsername(), status.toString()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAllLogs(Player player) {
		List<String> playersRecords = loggerRepository.findAllActivityRecords();

		if (playersRecords.isEmpty()) {
			System.out.println("\n*No logs.*\n");
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return;
		}

		for (String record : playersRecords) {
			System.out.println(record);
		}
		System.out.println();
		recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);
		if (optionalPlayer.isEmpty()) {
			System.out.printf("\n*Player %s not found*\n", inputUsernameForSearch);
			return;
		}

		Player findPlayer = optionalPlayer.get();

		List<String> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		if (playerLogs.isEmpty()) {
			System.out.printf("\n*No logs for player %s*\n", inputUsernameForSearch);
			return;
		}
		for (String record : playerLogs) {
			System.out.println(record);
		}
		System.out.println();
		recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
	}
}
