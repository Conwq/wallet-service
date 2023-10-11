package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.PlayerAccessService;
import org.example.walletservice.service.logger.PlayerActivityLogger;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

class PlayerAccessServiceImplTest {
	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private final PlayerActivityLogger playerActivityLogger = Mockito.mock(PlayerActivityLogger.class);
	private PlayerAccessService playerAccessService;
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		playerAccessService = new PlayerAccessServiceImpl(playerRepository, playerActivityLogger);

		final String username = "user123";
		final String password = "1313";
		player = new Player(username, password, Role.USER);

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				String.format("%s\n%s", AMOUNT, TRANSACTIONAL_TOKEN).getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	public void tearDown(){
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	void shouldRegistrationPlayer_successful() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.empty());

		playerAccessService.registrationPlayer(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		Mockito.verify(playerRepository, Mockito.times(1)).registrationPayer(player);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("User successfully registered!");
	}

	@Test
	public void shouldNotRegisteredPlayer_error() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.of(player));

		playerAccessService.registrationPlayer(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} This user is already registered.");
	}

	@Test
	public void shouldLogInPlayer_success() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.of(player));

		Player expected = playerAccessService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(expected).isEqualTo(player);
	}

	@Test
	public void shouldNotLogInPlayer_notFoundPlayer() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.empty());

		Player expected = playerAccessService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("{{FAIL}} Current player not found.");
		AssertionsForClassTypes.assertThat(expected).isNull();
	}

	@Test
	public void shouldNotLogInPlayer_invalidPassword() {
		Mockito.when(playerRepository.findPlayer(player.getUsername()))
				.thenReturn(Optional.of(new Player(player.getUsername(), "test", Role.USER)));

		Player expected = playerAccessService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("{{FAIL}} Incorrect password!");
		AssertionsForClassTypes.assertThat(expected).isNull();
	}



	@Test
	public void shouldShowAllLogs_successful(){
		List<String> allLogs = new ArrayList<>(){{
			add("log #1");
			add("log #2");
			add("log #3");
		}};

		Mockito.when(playerActivityLogger.getAllActivityRecords()).thenReturn(allLogs);

		playerAccessService.showAllLogs(player);

		Mockito.verify(playerActivityLogger, Mockito.times(1)).getAllActivityRecords();
		AssertionsForClassTypes.assertThat(outputStream.toString())
						.contains("log #1", "log #2", "log #3");
	}

	@Test
	public void shouldNotShowAllLogs_logsIsEmpty(){
		Mockito.when(playerActivityLogger.getAllActivityRecords()).thenReturn(new ArrayList<>());

		playerAccessService.showAllLogs(player);

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("*No logs*");
	}

	@Test
	public void shouldShowPlayerLogs_successful() {
		Mockito.when(playerActivityLogger.getActivityRecordsForPlayer(player.getUsername()))
				.thenReturn(new ArrayList<>(List.of("Transact #1", "Transact #2")));

		playerAccessService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Transact #1", "Transact #2");
	}

	@Test
	public void shouldNotShowPlayerLogs_playerNotFound() {
		Mockito.when(playerActivityLogger.getActivityRecordsForPlayer(player.getUsername())).thenReturn(null);

		playerAccessService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*Player %s not found*", player.getUsername()));
	}

	@Test
	public void shouldNotShowPlayerLogs_playerLogsIsEmpty() {
		Mockito.when(playerActivityLogger.getActivityRecordsForPlayer(player.getUsername())).thenReturn(new ArrayList<>());

		playerAccessService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*No logs for player %s*", player.getUsername()));
	}
}