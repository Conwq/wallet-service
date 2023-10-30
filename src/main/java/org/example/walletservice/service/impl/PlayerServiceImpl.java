package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.BalanceMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Provides functionality for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
@Service
public class PlayerServiceImpl implements PlayerService {

	private static final String PLAYER_EXIST_EXCEPTION = "This user is already registered. Try again.";
	private static final String PLAYER_NOT_FOUND = "Current player not found. Please try again.";
	private static final String INCORRECT_PASSWORD = "Incorrect password.";

	private final PlayerRepository playerRepository;
	private final PlayerMapper playerMapper;
	private final LoggerService loggerService;
	private final BalanceMapper balanceMapper;

	@Autowired
	public PlayerServiceImpl(PlayerRepository playerRepository,
							 PlayerMapper playerMapper,
							 LoggerService loggerService,
							 BalanceMapper balanceMapper) {
		this.playerRepository = playerRepository;
		this.playerMapper = playerMapper;
		this.loggerService = loggerService;
		this.balanceMapper = balanceMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(PlayerRequestDto playerRequestDto) {
		inputValidation(playerRequestDto);

		Optional<Player> optionalPlayer = findByUsername(playerRequestDto.username());

		if (optionalPlayer.isPresent()) {
			throw new PlayerAlreadyExistException(PLAYER_EXIST_EXCEPTION);
		}
		Player player = playerMapper.toEntityFromRequest(playerRequestDto);
		int playerID = playerRepository.registrationPayer(player);
		player.setPlayerID(playerID);

		loggerService.recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthPlayerDto logIn(PlayerRequestDto playerRequestDto) throws PlayerNotFoundException, InvalidInputDataException {
		inputValidation(playerRequestDto);

		Optional<Player> optionalPlayer = findByUsername(playerRequestDto.username());
		if (optionalPlayer.isEmpty()) {
			throw new PlayerNotFoundException(PLAYER_NOT_FOUND);
		}

		Player player = optionalPlayer.get();

		if (!player.getPassword().equals(playerRequestDto.password())) {
			loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.FAIL);
			throw new PlayerNotFoundException(INCORRECT_PASSWORD);
		}

		loggerService.recordActionInLog(Operation.LOG_IN, player, Status.SUCCESSFUL);
		return playerMapper.toAuthPlayerDto(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Player> findByUsername(String username) {
		return playerRepository.findPlayer(username);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BalanceResponseDto getPlayerBalance(AuthPlayerDto authPlayerDto) throws PlayerNotLoggedInException {
		if (authPlayerDto == null) {
			throw new PlayerNotLoggedInException("Performing an operation by an unregistered user.");
		}

		Player player = playerMapper.toEntity(authPlayerDto);
		Player findPlayer = playerRepository.findPlayerBalance(player);

		loggerService.recordActionInLog(Operation.VIEW_BALANCE, player, Status.SUCCESSFUL);
		return balanceMapper.toDto(findPlayer.getUsername(), findPlayer.getBalance());
	}

	/**
	 * Validates the input data in the provided PlayerRequestDto.
	 *
	 * @param playerRequestDto The PlayerRequestDto containing player information.
	 * @throws InvalidInputDataException If the input data is invalid, such as empty or too short username/password.
	 */
	private void inputValidation(PlayerRequestDto playerRequestDto) throws InvalidInputDataException {
		String username;
		String password;

		if (playerRequestDto == null) {
			throw new PlayerNotLoggedInException("Performing an operation by an unregistered user.");
		}

		username = playerRequestDto.username();
		password = playerRequestDto.password();

		if (username == null || password == null) {
			throw new InvalidInputDataException("Username or password can't be empty.");
		}
		if (username.length() < 1 || password.length() < 1) {
			throw new InvalidInputDataException("The length of the username or password cannot be less than 1");
		}
	}
}