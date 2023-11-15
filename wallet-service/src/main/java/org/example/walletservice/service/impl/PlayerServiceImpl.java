package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.PlayerEntity;
import org.example.walletservice.model.entity.RoleEntity;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.model.mapper.BalanceMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.RoleRepository;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
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
	private final JwtService jwtService;
	private final AuthenticationManager authenticationManager;
	private final PasswordEncoder passwordEncoder;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int registrationPlayer(PlayerRequestDto playerRequestDto)
			throws PlayerAlreadyExistException, InvalidInputDataException {
		inputValidation(playerRequestDto);

		playerRepository.findByUsername(playerRequestDto.username()).ifPresent(existingPlayer -> {
			throw new PlayerAlreadyExistException("This user is already registered.");
		});

		RoleEntity roleEntity = roleRepository.findByRoleName(Role.USER);
		PlayerEntity player = playerMapper.toEntityFromRequest(playerRequestDto, roleEntity);

		playerRepository.save(player);
		return player.getPlayerID();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String logIn(PlayerRequestDto playerRequestDto) throws PlayerNotFoundException, InvalidInputDataException {
		inputValidation(playerRequestDto);

		PlayerEntity playerEntity = playerRepository.findByUsername(playerRequestDto.username())
				.filter(player -> passwordEncoder.matches(playerRequestDto.password(), player.getPassword()))
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));

		authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(playerRequestDto.username(),
				playerRequestDto.password()));

		return jwtService.generateWebToken(playerEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BalanceResponseDto getPlayerBalance(UserDetails userDetails) throws PlayerNotFoundException {
		PlayerEntity playerEntity = playerRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));

		return balanceMapper.toDto(playerEntity);
	}

	/**
	 * Validates the input data in the provided PlayerRequestDto.
	 *
	 * @param playerRequestDto The PlayerRequestDto containing player information.
	 * @throws InvalidInputDataException If the input data is invalid, such as empty or too short username/password.
	 */
	private void inputValidation(PlayerRequestDto playerRequestDto) throws InvalidInputDataException {
		final String username;
		final String password;

		if (playerRequestDto == null) {
			throw new InvalidInputDataException("You need to fill in the data.");
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