package org.example.walletservice.service.impl;

import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
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
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String LOG_TEMPLATE =
			        """
					**************************
					-- Operation: %s;
					-- User: %s;
					-- Status: %s.
					""";
	private static final String NO_LOG = "*No logs.*\n";
	private static final String PLAYER_NOT_FOUND_TEMPLATE = "*Player %s not found*\n";
	private static final String NO_LOG_FOR_PLAYER_TEMPLATE = "*No logs for player %s*\n";

	public LoggerServiceImpl(LoggerRepository loggerRepository, PlayerRepository playerRepository) {
		this.loggerRepository = loggerRepository;
		this.playerRepository = playerRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordActionInLog(Operation operation, Player player, Status status) {
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), player.getUsername(),
				status.toString());

		Log log = Log.builder()
				.log(formatLog)
				.playerID(player.getPlayerID())
				.build();

		loggerRepository.recordAction(log);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAllLogs(Player player) {
		List<Log> playersRecords = loggerRepository.findAllActivityRecords();

		if (playersRecords == null) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return;
		}

		if (playersRecords.isEmpty()) {
			System.out.println(NO_LOG);
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return;
		}

		for (Log log : playersRecords) {
			System.out.println(log.getLog());
		}
		recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);

		if (optionalPlayer.isEmpty()) {
			System.out.printf(PLAYER_NOT_FOUND_TEMPLATE, inputUsernameForSearch);
			return;
		}

		Player findPlayer = optionalPlayer.get();

		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		if (playerLogs == null) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return;
		}

		if (playerLogs.isEmpty()) {
			System.out.printf(NO_LOG_FOR_PLAYER_TEMPLATE, inputUsernameForSearch);
			return;
		}
		for (Log log : playerLogs) {
			System.out.println(log.getLog());
		}
		recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
	}
}