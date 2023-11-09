package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.patseev.auditspringbootstarter.logger.model.Roles;

import java.util.List;
import java.util.Optional;

@Service
public class LoggerServiceImpl implements LoggerService {
	private final PlayerRepository playerRepository;
	private final LogMapper logMapper;
	private final PlayerMapper playerMapper;
	private final LoggerRepository loggerRepository;

	@Autowired
	public LoggerServiceImpl(PlayerRepository playerRepository,
							 LogMapper logMapper,
							 PlayerMapper playerMapper,
							 LoggerRepository loggerRepository) {
		this.playerRepository = playerRepository;
		this.logMapper = logMapper;
		this.playerMapper = playerMapper;
		this.loggerRepository = loggerRepository;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getAllLogs(AuthPlayer authPlayer) {
		Player player = userAuthorizationVerification(authPlayer);
		List<Log> playersRecords = loggerRepository.findAllActivityRecords();
		return playersRecords.stream().map(logMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayer authPlayer,
												  String inputUsernameForSearch) throws PlayerNotFoundException {
		Player player = userAuthorizationVerification(authPlayer);
		Player findPlayer = checkingForExistenceOfUser(inputUsernameForSearch);
		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		return playerLogs.stream().map(logMapper::toDto).toList();
	}

	/**
	 * Verifies user authorization based on the provided authentication DTO.
	 *
	 * @param authPlayer The authentication DTO.
	 * @return The authorized player.
	 * @throws PlayerNotLoggedInException       If the player is not logged in.
	 * @throws PlayerDoesNotHaveAccessException If the player does not have access to the resource.
	 */
	private Player userAuthorizationVerification(AuthPlayer authPlayer) {
		if (authPlayer == null) {
			throw new PlayerNotLoggedInException("Only an authorized administrator can perform this operation.");
		}

		if (authPlayer.role() != Roles.ADMIN) {
			throw new PlayerDoesNotHaveAccessException("You do not have access to this resource.");
		}

		return playerMapper.toEntity(authPlayer);
	}

	/**
	 * Checks for the existence of a player based on the provided optional and input username.
	 *
	 * @param inputUsernameForSearch The username used for the search.
	 * @return The player if present.
	 * @throws PlayerNotFoundException If the player is not found.
	 */
	private Player checkingForExistenceOfUser(String inputUsernameForSearch) throws PlayerNotFoundException {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);
		if (optionalPlayer.isEmpty()) {
			throw new PlayerNotFoundException(String.format("Player %s not found", inputUsernameForSearch));
		}
		return optionalPlayer.get();
	}
}