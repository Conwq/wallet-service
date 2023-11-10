package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.repository.rep.impl.PlayerRep;
import org.example.walletservice.repository.rep.impl.RoleRepository;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;

/**
 * Provides functionality for player registration, login, balance management, credit, debit, transaction history,
 * and log display.
 */
@Service
@RequiredArgsConstructor
public class PlayerServiceImpl implements PlayerService {
	private final PlayerRep playerRep;
	private final RoleRepository roleRepository;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void registrationPlayer(PlayerRequestDto playerRequestDto)
			throws PlayerAlreadyExistException, PlayerNotLoggedInException, InvalidInputDataException {
		inputValidation(playerRequestDto);

		if (playerRep.findByUsername(playerRequestDto.username()).isPresent()) {
			throw new PlayerAlreadyExistException("This user is already registered. Try again.");
		}

		PlayerEntity player = PlayerEntity.builder()
				.username(playerRequestDto.username())
				.password(playerRequestDto.password())
				.balance(BigDecimal.ZERO)
				.roleEntity(roleRepository.findByRoleName(Role.USER)).build();

		playerRep.save(player);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public AuthPlayer logIn(PlayerRequestDto playerRequestDto)
			throws PlayerNotFoundException, PlayerNotLoggedInException, InvalidInputDataException {
		inputValidation(playerRequestDto);
		PlayerEntity playerEntity = playerRep.findByUsername(playerRequestDto.username())
				.orElseThrow(() -> new PlayerNotFoundException("Current player not found. Please try again."));

		if (!playerEntity.getPassword().equals(playerRequestDto.password())) {
			throw new InvalidInputDataException("Incorrect password.");
		}

		return new AuthPlayer(
				playerEntity.getPlayerID(),
				playerEntity.getUsername(),
				playerEntity.getRoleEntity().getRole()
		);
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

		PlayerEntity player = playerRep.findByUsername(authPlayer.username())
				.orElseThrow(() -> new PlayerNotFoundException("Current player not found. Please try again."));

		return new BalanceResponseDto(player.getUsername(), player.getBalance());
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