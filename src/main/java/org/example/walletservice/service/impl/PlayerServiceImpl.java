package org.example.walletservice.service.impl;

import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.util.Optional;

/**
 * Implementation of the {@link PlayerService} interface that provides functionality
 * for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
public final class PlayerServiceImpl implements PlayerService {
	private final PlayerRepository playerRepository;
	private final LoggerService loggerService;

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
			System.out.println("*{{FAIL}} This user is already registered. Try again.*\n");
			return;
		}

		Player player = Player.builder()
				.username(username)
				.password(password)
				.role(Role.USER).build();

		int playerID = playerRepository.registrationPayer(player);
		if (playerID == -1) {
			System.out.println("The database is not available at the moment. Try again later.");
			return;
		}
		player.setPlayerID(playerID);

		System.out.println("*User successfully registered!*\n");
		loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player logIn(String username, String password) {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);

		if (optionalPlayer.isEmpty()) {
			System.out.println("*{{FAIL}} Current player not found. Please try again.*\n");
			return null;
		}

		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(password)) {
			System.out.println("*{{FAIL}} Incorrect password!*\n");
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
		double balance = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
		if (balance == -1) {
			System.out.println("The database is not available at the moment. Try again later.");
			return;
		}
		System.out.printf("*Balance -- %s*\n", balance);
		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
	}
}