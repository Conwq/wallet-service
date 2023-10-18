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

	public LoggerServiceImpl(LoggerRepository loggerRepository, PlayerRepository playerRepository) {
		this.loggerRepository = loggerRepository;
		this.playerRepository = playerRepository;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordActionInLog(Operation operation, Player player, Status status) {
		String formatLog = String.format("*****************\n-- Operation: %s;\n-- User: %s;\n-- Status: %s.\n",
				operation.toString(), player.getUsername(), status.toString());

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
			System.out.println("The database is not available at the moment. Try again later.");
			return;
		}

		if (playersRecords.isEmpty()) {
			System.out.println("\n*No logs.*\n");
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
			System.out.printf("*Player %s not found*\n", inputUsernameForSearch);
			return;
		}

		Player findPlayer = optionalPlayer.get();

		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		if (playerLogs == null) {
			System.out.println("The database is not available at the moment. Try again later.");
			return;
		}

		if (playerLogs.isEmpty()) {
			System.out.printf("*No logs for player %s*\n", inputUsernameForSearch);
			return;
		}
		for (Log log : playerLogs) {
			System.out.println(log.getLog());
		}
		recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
	}
}