package org.example.walletservice.service.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.Optional;

/**
 * Implementation of the {@link PlayerAccessService} interface that provides functionality
 * for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
public final class PlayerAccessServiceImpl implements PlayerAccessService {
	private final PlayerRepository playerRepository;
	private final PlayerActionLoggerService playerActionLoggerService;

	public PlayerAccessServiceImpl(PlayerRepository playerRepository, PlayerActionLoggerService playerActionLoggerService) {
		this.playerRepository = playerRepository;
		this.playerActionLoggerService = playerActionLoggerService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isPresent()) {
			System.out.println("\n*{{FAIL}} This user is already registered. Try again.*\n");

			playerActionLoggerService.recordAction(Operation.REGISTRATION, "UNKNOWN", Status.FAIL);
			return;
		}
		Player player = new Player(username, password, Role.USER);
		playerRepository.registrationPayer(player);
		System.out.println("\n*User successfully registered!*\n");

		playerActionLoggerService.recordAction(Operation.REGISTRATION, username, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()) {
			System.out.println("\n*{{FAIL}} Current player not found. Please try again.*\n");

			playerActionLoggerService.recordAction(Operation.LOG_IN, "UNKNOWN", Status.FAIL);
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)) {
			System.out.println("\n*{{FAIL}} Incorrect password!*\n");

			playerActionLoggerService.recordAction(Operation.LOG_IN, username, Status.FAIL);
			return null;
		}
		playerActionLoggerService.recordAction(Operation.LOG_IN, username, Status.SUCCESSFUL);
		return player;
	}
}