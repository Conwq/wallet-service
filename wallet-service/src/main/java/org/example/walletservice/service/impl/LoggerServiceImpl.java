package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.ent.entity.LogEntity;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.repository.rep.impl.LoggerRep;
import org.example.walletservice.repository.rep.impl.PlayerRep;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LoggerServiceImpl implements LoggerService {
	private final LoggerRep loggerRep;
	private final PlayerRep playerRep;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getAllLogs(AuthPlayer authPlayer)
			throws PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {
		userAuthorizationVerification(authPlayer);
		return loggerRep.findAll().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayer authPlayer, String inputUsernameForSearch)
			throws PlayerNotFoundException, PlayerNotLoggedInException, PlayerDoesNotHaveAccessException {

		userAuthorizationVerification(authPlayer);
		PlayerEntity foundPlayer = playerRep.findByUsername(inputUsernameForSearch)
				.orElseThrow(() -> new PlayerNotFoundException(String.format("Player %s not found", inputUsernameForSearch)));

		return foundPlayer.getLogEntity().stream().map(this::mapToDto).collect(Collectors.toList());
	}

	/**
	 * mapped Entity in Dto
	 * @param logEntity Entity to map in Dto
	 * @return Mapped Dto
	 */
	private LogResponseDto mapToDto(LogEntity logEntity) {
		return new LogResponseDto(logEntity.getLog());
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