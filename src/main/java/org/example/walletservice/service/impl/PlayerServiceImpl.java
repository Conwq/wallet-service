package org.example.walletservice.service.impl;

import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;

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
	private final PlayerMapper playerMapper;
	private static final String PLAYER_EXIST_EXCEPTION = "This user is already registered. Try again.";
	private static final String PLAYER_NOT_FOUND = "Current player not found. Please try again.";
	private static final String INCORRECT_PASSWORD = "Incorrect password.";

	public PlayerServiceImpl(PlayerRepository playerRepository, LoggerService loggerService,
							 PlayerMapper playerMapper) {
		this.playerRepository = playerRepository;
		this.loggerService = loggerService;
		this.playerMapper = playerMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(PlayerRequestDto playerRequestDto) {
		inputValidation(playerRequestDto);
		String username = playerRequestDto.username();
		Optional<Player> optionalPlayer = playerRepository.findPlayer(username);
		if (optionalPlayer.isPresent()) {
			throw new PlayerAlreadyExistException(PLAYER_EXIST_EXCEPTION);
		}
		Player player = playerMapper.toEntityFromRequest(playerRequestDto);
		int playerID = playerRepository.registrationPayer(player);
		if (playerID == -1) {
			System.out.println("[FAIL] Database error.");
			loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.FAIL);
			return;
		}
		player.setPlayerID(playerID);
		loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthPlayerDto logIn(PlayerRequestDto playerRequestDto) throws PlayerNotFoundException, InvalidInputDataException {
		inputValidation(playerRequestDto);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(playerRequestDto.username());
		if (optionalPlayer.isEmpty()) {
			throw new PlayerNotFoundException(PLAYER_NOT_FOUND);
		}
		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(playerRequestDto.password())) {
			throw new PlayerNotFoundException(INCORRECT_PASSWORD);
		}
		return playerMapper.toAuthPlayerDto(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getPlayerBalance(AuthPlayerDto authPlayerDto) {
		if (authPlayerDto == null) {
			throw new PlayerNotLoggedInException("Performing an operation by an unregistered user.");
		}

		Player player = playerMapper.toEntity(authPlayerDto);
		BigDecimal balance = playerRepository.findPlayerBalanceByPlayer(player);

		if (balance.equals(BigDecimal.valueOf(-1))) {
			System.out.println("[FAIL] Database error.");
			loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.FAIL);
			return null;
		}
		return balance;
	}

	public void inputValidation(PlayerRequestDto playerRequestDto) throws InvalidInputDataException {
		String username = playerRequestDto.username();
		String password = playerRequestDto.password();

		if (username == null || password == null) {
			throw new InvalidInputDataException("Username or password can`t be empty.");
		}
		if (username.length() < 1 || password.length() < 1) {
			throw new InvalidInputDataException("The length of the username or password cannot be less than 1");
		}
	}
}