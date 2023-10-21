package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.dto.AuthPlayerDto;
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
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String LOG_TEMPLATE =
			        """
					-Operation: %s-
					-User: %s-
					-Status: %s-
					""";
	private static final String NO_LOG = "*No logs.*\n";
	private static final String PLAYER_NOT_FOUND_TEMPLATE = "*Player %s not found*\n";
	private static final String NO_LOG_FOR_PLAYER_TEMPLATE = "*No logs for player %s*\n";

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
			System.out.println(ERROR_CONNECTION_DATABASE);
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return null;
		}
		if (playersRecords.isEmpty()) {
			System.out.println(NO_LOG);
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return new ArrayList<>(List.of(new LogResponseDto(NO_LOG)));
		}
		recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);
		return playersRecords.stream().map(logMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayerDto authPlayerDto, String inputUsernameForSearch)
			throws PlayerNotFoundException {
		Player player = playerMapper.toEntity(authPlayerDto);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);
		if (optionalPlayer.isEmpty()) {
			String playerNotFound = String.format(PLAYER_NOT_FOUND_TEMPLATE, inputUsernameForSearch);
			System.out.printf(playerNotFound);
			throw new PlayerNotFoundException(playerNotFound);
		}
		Player findPlayer = optionalPlayer.get();
		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		if (playerLogs == null) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.FAIL);
			return null;
		}
		if (playerLogs.isEmpty()) {
			String noLogForPlayer = String.format(NO_LOG_FOR_PLAYER_TEMPLATE, inputUsernameForSearch);
			System.out.printf(noLogForPlayer);
			recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
			return new ArrayList<>(List.of(new LogResponseDto(noLogForPlayer)));
		}
		List<LogResponseDto> logResponseDtos = new ArrayList<>(playerLogs.size());
		for(Log log : playerLogs){
			LogResponseDto responseDto = logMapper.toDto(log);
			logResponseDtos.add(responseDto);
		}
		recordActionInLog(Operation.SHOW_LOGS_PLAYER, player, Status.SUCCESSFUL);
		return logResponseDtos;
	}
}