package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.BalanceMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Provides functionality for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final PlayerRepository playerRepository;
	private final PlayerMapper playerMapper;
	private final BalanceMapper balanceMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(PlayerRequestDto playerRequestDto)
			throws PlayerAlreadyExistException, PlayerNotLoggedInException, InvalidInputDataException {

		inputValidation(playerRequestDto);

		Optional<Player> optionalPlayer = findByUsername(playerRequestDto.username());

		if (optionalPlayer.isPresent()) {
			throw new PlayerAlreadyExistException("This user is already registered. Try again.");
		}
		Player player = playerMapper.toEntityFromRequest(playerRequestDto);
		int playerID = playerRepository.registrationPayer(player);
		player.setPlayerID(playerID);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthPlayer logIn(PlayerRequestDto playerRequestDto)
			throws PlayerNotFoundException, PlayerNotLoggedInException, InvalidInputDataException {

		inputValidation(playerRequestDto);

		Optional<Player> optionalPlayer = findByUsername(playerRequestDto.username());
		if (optionalPlayer.isEmpty()) {
			throw new PlayerNotFoundException("Current player not found. Please try again.");
		}

		Player player = optionalPlayer.get();

		if (!player.getPassword().equals(playerRequestDto.password())) {
			throw new InvalidInputDataException("Incorrect password.");
		}

		return playerMapper.toAuthPlayer(player);
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
	public BalanceResponseDto getPlayerBalance(AuthPlayer authPlayer) throws PlayerNotLoggedInException {
		if (authPlayer == null) {
			throw new PlayerNotLoggedInException("Performing an operation by an unregistered user.");
		}

		Player player = playerMapper.toEntity(authPlayer);
		Player findPlayer = playerRepository.findPlayerBalance(player);

		return balanceMapper.toDto(findPlayer.getUsername(), findPlayer.getBalance());
	}

	/**
	 * Validates the input data in the provided PlayerRequestDto.
	 *
	 * @param playerRequestDto The PlayerRequestDto containing player information.
	 * @throws InvalidInputDataException If the input data is invalid, such as empty or too short username/password.
	 */
	private void inputValidation(PlayerRequestDto playerRequestDto) throws InvalidInputDataException, PlayerNotLoggedInException {
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