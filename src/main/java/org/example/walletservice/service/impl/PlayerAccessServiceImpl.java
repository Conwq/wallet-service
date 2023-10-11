package org.example.walletservice.service.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.logger.PlayerActivityLogger;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the {@link PlayerAccessService} interface that provides functionality
 * for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
public final class PlayerAccessServiceImpl implements PlayerAccessService {
	private final PlayerRepository playerRepository;
	private final PlayerActivityLogger playerActivityLogger;

	public PlayerAccessServiceImpl(PlayerRepository playerRepository, PlayerActivityLogger playerActivityLogger) {
		this.playerRepository = playerRepository;
		this.playerActivityLogger = playerActivityLogger;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(String username, String password){
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if(optionalPlayer.isPresent()){
			System.out.println("\n*{{FAIL}} This user is already registered. Try again.*\n");

			playerActivityLogger.recordAction(Operation.REGISTRATION, "UNKNOWN", Status.FAIL);
			return;
		}
		Player player = new Player(username, password, Role.USER);
		playerRepository.registrationPayer(player);
		System.out.println("\n*User successfully registered!*\n");

		playerActivityLogger.recordAction(Operation.REGISTRATION, username, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()){
			System.out.println("\n*{{FAIL}} Current player not found. Please try again.*\n");

			playerActivityLogger.recordAction(Operation.LOG_IN, "UNKNOWN", Status.FAIL);
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)){
			System.out.println("\n*{{FAIL}} Incorrect password!*\n");

			playerActivityLogger.recordAction(Operation.LOG_IN, username, Status.FAIL);
			return null;
		}
		playerActivityLogger.recordAction(Operation.LOG_IN, username, Status.SUCCESSFUL);
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showAllLogs(Player player) {
		List<String> allLogs = playerActivityLogger.getAllActivityRecords();

		if (allLogs.isEmpty()){
			System.out.println("\n*No logs*\n");
			playerActivityLogger.recordAction(Operation.SHOW_ALL_LOGS, player.getUsername(), Status.FAIL);
			return;
		}

		for (String record : allLogs){
			System.out.println(record);
		}
		System.out.println();
		playerActivityLogger.recordAction(Operation.SHOW_ALL_LOGS, player.getUsername(), Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void showLogsByUsername(Player player, String inputUsernameForSearch) {
		List<String> playerLogs = playerActivityLogger.getActivityRecordsForPlayer(inputUsernameForSearch);
		if (playerLogs == null){
			System.out.printf("\n*Player %s not found*\n", inputUsernameForSearch);
			return;
		}
		if (playerLogs.isEmpty()){
			System.out.printf("\n*No logs for player %s*\n", inputUsernameForSearch);
			return;
		}

		for (String record : playerLogs){
			System.out.println(record);
		}
		System.out.println();
		playerActivityLogger.recordAction(Operation.SHOW_LOGS_PLAYER, player.getUsername(), Status.SUCCESSFUL);
	}
}