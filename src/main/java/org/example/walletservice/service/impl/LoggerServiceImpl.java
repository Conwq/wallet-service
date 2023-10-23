package org.example.walletservice.service.impl;

import org.aspectj.lang.annotation.Pointcut;
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
import org.example.walletservice.service.exception.PlayerNotFoundException;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LoggerServiceImpl implements LoggerService {
	private final LoggerRepository loggerRepository;
	private final PlayerRepository playerRepository;
	private final LogMapper logMapper;
	private final PlayerMapper playerMapper;
	private static final String LOG_TEMPLATE =
			"""
					-Operation: %s-
					-User: %s-
					-Status: %s-
					""";
	private static final String NO_LOG = "No logs.";
	private static final String PLAYER_NOT_FOUND_TEMPLATE = "Player %s not found";
	private static final String NO_LOG_FOR_PLAYER_TEMPLATE = "No logs for player %s";

	public LoggerServiceImpl(LoggerRepository loggerRepository, PlayerRepository playerRepository,
							 LogMapper logMapper, PlayerMapper playerMapper) {
		this.loggerRepository = loggerRepository;
		this.playerRepository = playerRepository;
		this.logMapper = logMapper;
		this.playerMapper = playerMapper;
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
		Player player = playerMapper.toEntity(authPlayerDto);
		List<Log> playersRecords = loggerRepository.findAllActivityRecords();

		if (playersRecords == null) {
			System.out.println("[FAIL] Database error.");
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return null;
		}

		if (playersRecords.isEmpty()) {
			Log log = new Log();
			log.setLog(NO_LOG);
			playersRecords.add(log);
		}
		return playersRecords.stream().map(logMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayerDto authPlayerDto, String inputUsernameForSearch)
			throws PlayerNotFoundException {
		Player player = playerMapper.toEntity(authPlayerDto);
		Player findPlayer = checkingForExistenceOfUser(inputUsernameForSearch);

		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());

		if (playerLogs == null) {
			System.out.println("[FAIL] Database error.");
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.FAIL);
			return null;
		}

		if (playerLogs.isEmpty()) {
			Log log = new Log();
			log.setLog(String.format(NO_LOG_FOR_PLAYER_TEMPLATE, inputUsernameForSearch));
			playerLogs.add(log);
		}
		return playerLogs.stream().map(logMapper::toDto).toList();
	}

	/**
	 * Checks for the existence of a player based on the provided optional and input username.
	 *
	 * @param inputUsernameForSearch  The username used for the search.
	 * @return The player if present.
	 * @throws PlayerNotFoundException If the player is not found.
	 */
	private Player checkingForExistenceOfUser(String inputUsernameForSearch) throws PlayerNotFoundException {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);

		if (optionalPlayer.isEmpty()) {
			throw new PlayerNotFoundException(String.format(PLAYER_NOT_FOUND_TEMPLATE, inputUsernameForSearch));
		}
		return optionalPlayer.get();
	}
}