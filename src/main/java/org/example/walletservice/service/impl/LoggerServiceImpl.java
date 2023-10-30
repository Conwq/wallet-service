package org.example.walletservice.service.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LoggerServiceImpl implements LoggerService {

	private final PlayerRepository playerRepository;
	private final LogMapper logMapper;
	private final PlayerMapper playerMapper;
	private final LoggerRepository loggerRepository;

	private static final String LOG_TEMPLATE =
			"""
					-Operation: %s-
					-User: %s-
					-Status: %s-
					""";

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
	public void recordActionInLog(Operation operation, Player player, Status status) {
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), player.getUsername(), status.toString());
		Log log = logMapper.toEntity(formatLog, player.getPlayerID());
		loggerRepository.recordAction(log);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getAllLogs(AuthPlayerDto authPlayerDto) {
		Player player = userAuthorizationVerification(authPlayerDto);
		List<Log> playersRecords = loggerRepository.findAllActivityRecords();
		recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);
		return playersRecords.stream().map(logMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayerDto authPlayerDto,
												  String inputUsernameForSearch) throws PlayerNotFoundException {
		Player player = userAuthorizationVerification(authPlayerDto);
		Player findPlayer = checkingForExistenceOfUser(player, inputUsernameForSearch);
		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
		return playerLogs.stream().map(logMapper::toDto).toList();
	}

	/**
	 * Verifies user authorization based on the provided authentication DTO.
	 *
	 * @param authPlayerDto The authentication DTO.
	 * @return The authorized player.
	 * @throws PlayerNotLoggedInException       If the player is not logged in.
	 * @throws PlayerDoesNotHaveAccessException If the player does not have access to the resource.
	 */
	private Player userAuthorizationVerification(AuthPlayerDto authPlayerDto) {
		if (authPlayerDto == null) {
			throw new PlayerNotLoggedInException("Only an authorized administrator can perform this operation.");
		}

		if (authPlayerDto.role() != Role.ADMIN) {
			throw new PlayerDoesNotHaveAccessException("You do not have access to this resource.");
		}

		return playerMapper.toEntity(authPlayerDto);
	}

	/**
	 * Checks for the existence of a player based on the provided optional and input username.
	 *
	 * @param inputUsernameForSearch The username used for the search.
	 * @return The player if present.
	 * @throws PlayerNotFoundException If the player is not found.
	 */
	private Player checkingForExistenceOfUser(Player player,
											  String inputUsernameForSearch) throws PlayerNotFoundException {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);
		if (optionalPlayer.isEmpty()) {
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.FAIL);
			throw new PlayerNotFoundException(String.format("Player %s not found", inputUsernameForSearch));
		}
		return optionalPlayer.get();
	}
}