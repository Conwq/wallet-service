package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.model.ent.entity.RoleEntity;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.model.mapper.BalanceMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.RoleRepository;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

/**
 * Provides functionality for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final PlayerRepository playerRepository;
	private final RoleRepository roleRepository;
	private final PlayerMapper playerMapper;
	private final BalanceMapper balanceMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(PlayerRequestDto playerRequestDto)
			throws PlayerAlreadyExistException, PlayerNotLoggedInException, InvalidInputDataException {
		inputValidation(playerRequestDto);

		if (playerRepository.findByUsername(playerRequestDto.username()).isPresent()) {
			throw new PlayerAlreadyExistException("This user is already registered. Try again.");
		}

		RoleEntity roleEntity = roleRepository.findByRoleName(Role.USER);
		PlayerEntity player = playerMapper.toEntityFromRequest(playerRequestDto, roleEntity);

		playerRepository.save(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthPlayer logIn(PlayerRequestDto playerRequestDto)
			throws PlayerNotFoundException, PlayerNotLoggedInException, InvalidInputDataException {
		inputValidation(playerRequestDto);
		PlayerEntity playerEntity = playerRepository.findByUsername(playerRequestDto.username())
				.orElseThrow(() -> new PlayerNotFoundException("Current player not found. Please try again."));

		if (!playerEntity.getPassword().equals(playerRequestDto.password())) {
			throw new InvalidInputDataException("Incorrect password.");
		}

		return playerMapper.toAuthPlayer(playerEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BalanceResponseDto getPlayerBalance(AuthPlayer authPlayer)
			throws PlayerNotLoggedInException, PlayerNotFoundException {
		if (authPlayer == null) {
			throw new PlayerNotLoggedInException("Performing an operation by an unregistered user.");
		}

		PlayerEntity playerEntity = playerRepository.findByUsername(authPlayer.username())
				.orElseThrow(() -> new PlayerNotFoundException("Current player not found. Please try again."));

		return balanceMapper.toDto(playerEntity);
	}

	/**
	 * Validates the input data in the provided PlayerRequestDto.
	 *
	 * @param playerRequestDto The PlayerRequestDto containing player information.
	 * @throws InvalidInputDataException If the input data is invalid, such as empty or too short username/password.
	 */
	private void inputValidation(PlayerRequestDto playerRequestDto)
			throws InvalidInputDataException, PlayerNotLoggedInException {

		final String username;
		final String password;

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