package org.example.walletservice.service.impl;

import org.assertj.core.api.Assertions;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@Disabled
@SpringBootTest
class LoggerServiceImplTest {
//	@MockBean
//	private LoggerRepository loggerRepository;
//	@MockBean
//	private PlayerRepository playerRepository;
//	private final LoggerService loggerService;
//	private LogMapper logMapper;
//	private Player player;
//	private AuthPlayer authPlayer;
//
//	@Autowired
//	public LoggerServiceImplTest(LoggerService loggerService) {
//		this.loggerService = loggerService;
//	}
//
//	@BeforeEach
//	void setUp() {
//		logMapper = Mappers.getMapper(LogMapper.class);
//
//		player = new Player();
//		player.setPlayerID(1);
//		player.setUsername("user123");
//		player.setPassword("2312");
//		player.setRole(Role.USER);
//
//		authPlayer = new AuthPlayer(1, "admin", Role.ADMIN);
//	}
//
//	@Test
//	@DisplayName("Should successfully return all logs ")
//	public void shouldReturnAllLogs_successful() {
////		Log log = new Log();
////		log.setLog("log #1");
////
////		List<Log> logs = Collections.singletonList(log);
////		Mockito.when(loggerRepository.findAllActivityRecords()).thenReturn(logs);
////
////		List<LogResponseDto> result = loggerService.getAllLogs(authPlayer);
//
////		assertEquals(logMapper.toDto(logs.get(0)), result.get(0));
//	}
//
//	@Test
//	@DisplayName("Must successfully return a specific player's logs")
//	public void shouldReturnPlayerLogs_successful() {
//		Log first = new Log();
//		first.setLog("log #1");
//		first.setPlayerID(player.getPlayerID());
//
//		Log second = new Log();
//		second.setLog("log #2");
//		second.setPlayerID(player.getPlayerID());
//
//		Mockito.when(playerRepository.findPlayer(Mockito.any(String.class))).
//				thenReturn(Optional.of(player));
//		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
//				.thenReturn(new ArrayList<>(List.of(first, second)));
//
//		List<LogResponseDto> logsByUsername = loggerService.getLogsByUsername(authPlayer, player.getUsername());
//
//		Assertions.assertThat(logsByUsername)
//				.extracting(LogResponseDto::record)
//				.contains(first.getLog(), second.getLog());
//	}
//
//	@Test
//	@DisplayName("Should not show player logs as they have not been found")
//	public void shouldNotShowPlayerLogs_playerNotFound() {
//		String inputUsernameForSearch = "username";
//		Mockito.when(playerRepository.findPlayer(inputUsernameForSearch)).thenReturn(Optional.empty());
//
//		assertThrows(PlayerNotFoundException.class, () -> {
//			loggerService.getLogsByUsername(authPlayer, inputUsernameForSearch);
//		});
//	}
}