package org.example.walletservice.service.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerAccessService;
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
	private final LoggerService loggerService;

	public PlayerAccessServiceImpl(PlayerRepository playerRepository, LoggerService loggerService) {
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
			System.out.println("\n*{{FAIL}} This user is already registered. Try again.*\n");
			return;
		}

		Player player = Player.builder()
				.username(username)
				.password(password)
				.role(Role.USER)
				.balance(0.0).build();

		int playerID = playerRepository.registrationPayer(player);
		player.setPlayerID(playerID);

		System.out.println("\n*User successfully registered!*\n");
		loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()) {
			System.out.println("\n*{{FAIL}} Current player not found. Please try again.*\n");
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)) {
			System.out.println("\n*{{FAIL}} Incorrect password!*\n");
			return null;
		}
		loggerService.recordActionInLog(Operation.LOG_IN, player, Status.SUCCESSFUL);
		return player;
	}
}