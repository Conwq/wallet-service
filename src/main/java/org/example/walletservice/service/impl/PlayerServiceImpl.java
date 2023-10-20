package org.example.walletservice.service.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.math.BigDecimal;
import java.util.Optional;

/**
 * Provides functionality
 * for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
public final class PlayerServiceImpl implements PlayerService {
	private final PlayerRepository playerRepository;
	private final LoggerService loggerService;
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String USER_EXIST_ERROR = "*{{FAIL}} This user is already registered. Try again.*\n";
	private static final String SUCCESSFUL_REGISTRATION = "*User successfully registered!*\n";
	private static final String PLAYER_NOT_FOUND = "*{{FAIL}} Current player not found. Please try again.*\n";
	private static final String INCORRECT_PASSWORD = "*{{FAIL}} Incorrect password!*\n";
	private static final String BALANCE_TEMPLATE = "*Balance -- %s*\n";

	public PlayerServiceImpl(PlayerRepository playerRepository, LoggerService loggerService) {
		this.playerRepository = playerRepository;
		this.loggerService = loggerService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isPresent()) {
			System.out.println(USER_EXIST_ERROR);
			return;
		}

		Player player = Player.builder()
				.username(username)
				.password(password)
				.role(Role.USER).build();

		int playerID = playerRepository.registrationPayer(player);
		if (playerID == -1) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return;
		}
		player.setPlayerID(playerID);

		System.out.println(SUCCESSFUL_REGISTRATION);
		loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()) {
			System.out.println(PLAYER_NOT_FOUND);
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)) {
			System.out.println(INCORRECT_PASSWORD);
			return null;
		}
		loggerService.recordActionInLog(Operation.LOG_IN, player, Status.SUCCESSFUL);
		return player;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void displayPlayerBalance(Player player) {
		BigDecimal balance = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
		if (balance.equals(BigDecimal.valueOf(-1))) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return;
		}
		System.out.printf(BALANCE_TEMPLATE, balance);
		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
	}
}