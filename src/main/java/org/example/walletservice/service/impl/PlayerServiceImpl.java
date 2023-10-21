package org.example.walletservice.service.impl;

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
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String USER_EXIST_EXCEPTION = "*{{FAIL}} This user is already registered. Try again.*\n";
	private static final String SUCCESSFUL_REGISTRATION = "*User successfully registered!*\n";
	private static final String PLAYER_NOT_FOUND = "*{{FAIL}} Current player not found. Please try again.*\n";
	private static final String INCORRECT_PASSWORD = "*{{FAIL}} Incorrect password!*\n";

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
			System.out.println(USER_EXIST_EXCEPTION);
			throw new PlayerAlreadyExistException(USER_EXIST_EXCEPTION);
		}
		Player player = playerMapper.toEntityFromRequest(playerRequestDto);
		int playerID = playerRepository.registrationPayer(player);
		if (playerID == -1) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.FAIL);
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
	public AuthPlayerDto logIn(PlayerRequestDto playerRequestDto) throws PlayerNotFoundException, InvalidInputDataException {
		inputValidation(playerRequestDto);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(playerRequestDto.username());
		if (optionalPlayer.isEmpty()) {
			System.out.println(PLAYER_NOT_FOUND);
			throw new PlayerNotFoundException(PLAYER_NOT_FOUND);
		}
		Player player = optionalPlayer.get();
		if (!player.getPassword().equals(playerRequestDto.password())) {
			System.out.println(INCORRECT_PASSWORD);
			loggerService.recordActionInLog(Operation.LOG_IN, player, Status.FAIL);
			throw new PlayerNotFoundException(INCORRECT_PASSWORD);
		}
		loggerService.recordActionInLog(Operation.LOG_IN, player, Status.SUCCESSFUL);
		return playerMapper.toAuthPlayerDto(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal getPlayerBalance(AuthPlayerDto authPlayerDto) {
		Player player = playerMapper.toEntity(authPlayerDto);
		BigDecimal balance = playerRepository.findPlayerBalanceByPlayer(player);
		if (balance.equals(BigDecimal.valueOf(-1))) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.FAIL);
			return null;
		}
		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
		return balance;
	}
t add
	public void inputValidation (PlayerRequestDto playerRequestDto) throws InvalidInputDataException{
		String username = playerRequestDto.username();
		String password = playerRequestDto.password();

		if(username == null || password == null){
			throw new InvalidInputDataException("Username or password can`t be empty.");
		}
		if (username.length() < 1 || password.length() < 1) {
			throw new InvalidInputDataException("The length of the username or password cannot be less than 1");
		}
	}
}