package org.example.walletservice.service.impl;

import org.example.walletservice.repository.PlayerActionLoggerRepository;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class PlayerActionLoggerServiceImpl implements PlayerActionLoggerService {
	private final PlayerActionLoggerRepository playerActionLoggerRepository;

	public PlayerActionLoggerServiceImpl(PlayerActionLoggerRepository playerActionLoggerRepository) {
		this.playerActionLoggerRepository = playerActionLoggerRepository;
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordAction(Operation operation, String username, Status status) {
		List<String> playersActionLogg = playerActionLoggerRepository.findActivityRecordsForPlayer(username);
		if (playersActionLogg == null) {
			playersActionLogg = new ArrayList<>();
		}

		playersActionLogg.add(String.format("--Operation: %s; \t--User: %s; \t--Status: %s.",
				operation.toString(), username, status.toString()));

		playerActionLoggerRepository.recordAction(username, playersActionLogg);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAllLogs(String username) {
		Map<String, List<String>> allActivityRecords = playerActionLoggerRepository.findAllActivityRecords();

		if (allActivityRecords.isEmpty()) {
			System.out.println("\n*No logs*\n");
			recordAction(Operation.SHOW_ALL_LOGS, username, Status.FAIL);
			return;
		}

		for (Map.Entry<String, List<String>> playersRecords : allActivityRecords.entrySet()) {
			List<String> records = playersRecords.getValue();
			for (String record : records) {
				System.out.println(record);
			}
		}
		System.out.println();

		recordAction(Operation.SHOW_ALL_LOGS, username, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLogsByUsername(String username, String inputUsernameForSearch) {
		List<String> playerLogs = playerActionLoggerRepository.findActivityRecordsForPlayer(inputUsernameForSearch);
		if (playerLogs == null) {
			System.out.printf("\n*Player %s not found*\n", inputUsernameForSearch);
			return;
		}
		if (playerLogs.isEmpty()) {
			System.out.printf("\n*No logs for player %s*\n", inputUsernameForSearch);
			return;
		}

		for (String record : playerLogs) {
			System.out.println(record);
		}
		System.out.println();

		recordAction(Operation.SHOW_LOGS_PLAYER, username, Status.SUCCESSFUL);
	}
}
