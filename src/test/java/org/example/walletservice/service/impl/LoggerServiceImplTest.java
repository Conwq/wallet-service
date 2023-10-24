package org.example.walletservice.service.impl;

import org.assertj.core.api.Assertions;
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
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class LoggerServiceImplTest {
	private LoggerService loggerService;
	private LoggerRepository loggerRepository;
	private PlayerRepository playerRepository;
	private LogMapper logMapper;
	private PlayerMapper playerMapper;
	private Player player;
	private AuthPlayerDto authPlayerDto;

	@BeforeEach
	void setUp() {
		logMapper = LogMapper.instance;
		playerMapper = Mockito.mock(PlayerMapper.class);
		playerRepository = Mockito.mock(PlayerRepository.class);
		loggerRepository = Mockito.mock(LoggerRepository.class);
		loggerService = new LoggerServiceImpl(loggerRepository, playerRepository, logMapper, playerMapper);

		player = new Player();
		player.setPlayerID(1);
		player.setUsername("user123");
		player.setPassword("2312");
		player.setRole(Role.USER);

		authPlayerDto = new AuthPlayerDto(1, "username", Role.ADMIN);
	}

	@Test
	public void shouldReturnAllLogs_successful() {
		Log log = new Log();
		log.setLog("log #1");

		List<Log> logs = Collections.singletonList(log);
		Mockito.when(loggerRepository.findAllActivityRecords()).thenReturn(logs);

		List<LogResponseDto> result = loggerService.getAllLogs(authPlayerDto);

		assertEquals(logMapper.toDto(logs.get(0)), result.get(0));
	}

	@Test
	public void shouldReturnPlayerLogs_successful() {
		Log first = new Log();
		first.setLog("log #1");
		first.setPlayerID(player.getPlayerID());

		Log second = new Log();
		second.setLog("log #2");
		second.setPlayerID(player.getPlayerID());

		Mockito.when(playerRepository.findPlayer(Mockito.any(String.class))).
				thenReturn(Optional.of(player));
		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
				.thenReturn(new ArrayList<>(List.of(first, second)));

		List<LogResponseDto> logsByUsername = loggerService.getLogsByUsername(authPlayerDto, player.getUsername());

		Assertions.assertThat(logsByUsername)
				.extracting(LogResponseDto::record)
				.contains(first.getLog(), second.getLog());
	}

	@Test
	public void shouldNotShowPlayerLogs_playerNotFound() {
		String inputUsernameForSearch = "username";
		Mockito.when(playerRepository.findPlayer(inputUsernameForSearch)).thenReturn(Optional.empty());

		assertThrows(PlayerNotFoundException.class, () -> {
			loggerService.getLogsByUsername(authPlayerDto, inputUsernameForSearch);
		});
	}

	@Test
	public void shouldRecordLog() {
		loggerService.recordActionInLog(Operation.SHOW_ALL_LOGS, player, Status.SUCCESSFUL);

		Mockito.verify(loggerRepository).recordAction(Mockito.any(Log.class));
	}
}