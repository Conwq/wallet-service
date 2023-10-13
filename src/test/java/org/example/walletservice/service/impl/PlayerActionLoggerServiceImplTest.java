package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerActionLoggerRepository;
import org.example.walletservice.service.PlayerActionLoggerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

class PlayerActionLoggerServiceImplTest {
	private PlayerActionLoggerService playerActionLoggerService;
	private final PlayerActionLoggerRepository playerActionLoggerRepository =
			Mockito.mock(PlayerActionLoggerRepository.class);
	private final PrintStream origOut = System.out;
	private Player player;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	void setUp() {
		playerActionLoggerService = new PlayerActionLoggerServiceImpl(playerActionLoggerRepository);

		player = new Player("user123", "2312", Role.USER);

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));
	}

	@AfterEach
	void tearDown() {
		System.setOut(origOut);
	}

	@Test
	public void shouldShowAllLogs_successful(){
		List<String> logsPlayer1 = new ArrayList<>(List.of("log #1"));
		List<String> logsPlayer2 = new ArrayList<>(List.of("log #2"));
		List<String> logsPlayer3 = new ArrayList<>(List.of("log #3"));

		Map<String, List<String>> allActivityRecords = new TreeMap<>(){{
			put("player1", logsPlayer1);
			put("player2", logsPlayer2);
			put("player3", logsPlayer3);
		}};

		Mockito.when(playerActionLoggerRepository.findAllActivityRecords()).thenReturn(allActivityRecords);

		playerActionLoggerService.showAllLogs(player.getUsername());

		Mockito.verify(playerActionLoggerRepository, Mockito.times(1)).findAllActivityRecords();
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("log #1", "log #2", "log #3");
	}

	@Test
	public void shouldNotShowAllLogs_logsIsEmpty(){
		Mockito.when(playerActionLoggerRepository.findAllActivityRecords()).thenReturn(new TreeMap<>());

		playerActionLoggerService.showAllLogs(player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("*No logs*");
	}

	@Test
	public void shouldShowPlayerLogs_successful() {
		Mockito.when(playerActionLoggerRepository.findActivityRecordsForPlayer(player.getUsername()))
				.thenReturn(new ArrayList<>(List.of("Transact #1", "Transact #2")));

		playerActionLoggerService.showLogsByUsername(player.getUsername(), player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Transact #1", "Transact #2");
	}

	@Test
	public void shouldNotShowPlayerLogs_playerNotFound() {
		Mockito.when(playerActionLoggerRepository.findActivityRecordsForPlayer(player.getUsername()))
				.thenReturn(null);

		playerActionLoggerService.showLogsByUsername(player.getUsername(), player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*Player %s not found*", player.getUsername()));
	}

	@Test
	public void shouldNotShowPlayerLogs_playerLogsIsEmpty() {
		Mockito.when(playerActionLoggerRepository.findActivityRecordsForPlayer(player.getUsername()))
				.thenReturn(new ArrayList<>());

		playerActionLoggerService.showLogsByUsername(player.getUsername(), player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*No logs for player %s*", player.getUsername()));
	}
}