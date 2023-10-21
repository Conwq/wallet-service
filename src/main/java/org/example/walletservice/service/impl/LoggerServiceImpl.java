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
		String formatLog = String.format(LOG_TEMPLATE, operation.toString(), player.getUsername(),
				status.toString());
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
			return null;
		}
		if (playersRecords.isEmpty()) {
			System.out.println(NO_LOG);
			recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.FAIL);
			return null;
		}
		List<LogResponseDto> logResponseDtos = new ArrayList<>(playersRecords.size());
		for(Log log : playersRecords){
			LogResponseDto responseDto = logMapper.toDto(log);
			logResponseDtos.add(responseDto);
		}
		recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);
		return logResponseDtos;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<LogResponseDto> getLogsByUsername(AuthPlayerDto authPlayerDto, String inputUsernameForSearch) {
		Player player = playerMapper.toEntity(authPlayerDto);
		Optional<Player> optionalPlayer = playerRepository.findPlayer(inputUsernameForSearch);
		if (optionalPlayer.isEmpty()) {
			System.out.printf(PLAYER_NOT_FOUND_TEMPLATE, inputUsernameForSearch);
			return null;
		}
		Player findPlayer = optionalPlayer.get();
		List<Log> playerLogs = loggerRepository.findActivityRecordsForPlayer(findPlayer.getPlayerID());
		if (playerLogs == null) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return null;
		}
		if (playerLogs.isEmpty()) {
			System.out.printf(NO_LOG_FOR_PLAYER_TEMPLATE, inputUsernameForSearch);
			return null;
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