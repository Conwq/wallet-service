package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class LoggerServiceImplTest {
	private LoggerService loggerService;
	private final LoggerRepository loggerRepository = Mockito.mock(LoggerRepository.class);
	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private final PrintStream origOut = System.out;
	private Player player;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		loggerService = new LoggerServiceImpl(loggerRepository, playerRepository);

		player = Player.builder()
				.playerID(1)
				.username("user123")
				.password("2312")
				.role(Role.USER).build();

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(origOut);
	}

	@Test
	public void shouldShowAllLogs_successful() {
		List<String> logsPlayer = new ArrayList<>(List.of("log #1", "log #2"));

		Mockito.when(loggerRepository.findAllActivityRecords()).thenReturn(logsPlayer);

		loggerService.showAllLogs(player);

		Mockito.verify(loggerRepository, Mockito.times(1)).findAllActivityRecords();
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("log #1", "log #2");
	}

	@Test
	public void shouldNotShowAllLogs_logsIsEmpty() {
		Mockito.when(loggerRepository.findAllActivityRecords()).thenReturn(new ArrayList<>());

		loggerService.showAllLogs(player);

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("*No logs.*");
	}

	@Test
	public void shouldShowPlayerLogs_successful() {
		Mockito.when(playerRepository.findPlayer(Mockito.any(String.class))).
				thenReturn(Optional.of(player));
		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
				.thenReturn(new ArrayList<>(List.of("Transact #1", "Transact #2")));

		loggerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Transact #1", "Transact #2");
	}

	@Test
	public void shouldNotShowPlayerLogs_playerNotFound() {
		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
				.thenReturn(null);

		loggerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*Player %s not found*", player.getUsername()));
	}

	@Test
	public void shouldNotShowPlayerLogs_playerLogsIsEmpty() {
		Mockito.when(playerRepository.findPlayer(Mockito.any(String.class))).
				thenReturn(Optional.of(player));
		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
				.thenReturn(new ArrayList<>());

		loggerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*No logs for player %s*", player.getUsername()));
	}
}