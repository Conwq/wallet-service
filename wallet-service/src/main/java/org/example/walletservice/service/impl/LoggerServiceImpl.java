package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggerServiceImpl implements LoggerService {
	private final LoggerRepository loggerRepository;
	private final PlayerRepository playerRepository;
	private final LogMapper logMapper;

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public List<LogResponseDto> getAllLogs(AuthPlayer authPlayer)
			throws PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {
		userAuthorizationVerification(authPlayer);
		return loggerRepository.findAll().stream().map(logMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional(readOnly = true)
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayer authPlayer, String inputUsernameForSearch)
			throws PlayerNotFoundException, PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {

		userAuthorizationVerification(authPlayer);
		PlayerEntity foundPlayer = playerRepository.findByUsername(inputUsernameForSearch)
				.orElseThrow(() -> new PlayerNotFoundException(String.format("Player %s not found", inputUsernameForSearch)));
		return foundPlayer.getLogEntity().stream().map(logMapper::toDto).collect(Collectors.toList());
	}

	/**
	 * Verifies user authorization based on the provided authentication DTO.
	 *
	 * @param authPlayer The authentication DTO.
	 * @throws PlayerNotLoggedInException       If the player is not logged in.
	 * @throws PlayerDoesNotHaveAccessException If the player does not have access to the resource.
	 */
	private void userAuthorizationVerification(AuthPlayer authPlayer) {
		if (authPlayer == null) {
			throw new PlayerNotLoggedInException("Only an authorized administrator can perform this operation.");
		}
		if (authPlayer.role() != Role.ADMIN) {
			throw new PlayerDoesNotHaveAccessException("You do not have access to this resource.");
		}
	}
}