package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Optional;

class PlayerServiceTest {
	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private final LoggerService loggerService = Mockito.mock(LoggerServiceImpl.class);
	private PlayerService playerService;
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		playerService = new PlayerServiceImpl(playerRepository, loggerService);

		player = Player.builder()
				.playerID(1)
				.username("user123")
				.password("1313")
				.role(Role.USER).build();

		outputStream = new ByteArrayOutputStream();
		System.setOut(new PrintStream(outputStream));

		ByteArrayInputStream inputStream = new ByteArrayInputStream(
				String.format("%s\n%s", AMOUNT, TRANSACTIONAL_TOKEN).getBytes());
		System.setIn(inputStream);
	}

	@AfterEach
	public void tearDown() {
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	void shouldRegistrationPlayer_successful() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.empty());

		playerService.registrationPlayer(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("User successfully registered!");
	}

	@Test
	public void shouldNotRegisteredPlayer_error() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.of(player));

		playerService.registrationPlayer(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} This user is already registered.");
	}

	@Test
	public void shouldLogInPlayer_success() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.of(player));

		Player expected = playerService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(expected).isEqualTo(player);
	}

	@Test
	public void shouldNotLogInPlayer_notFoundPlayer() {
		Mockito.when(playerRepository.findPlayer(player.getUsername())).thenReturn(Optional.empty());

		Player expected = playerService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("{{FAIL}} Current player not found.");
		AssertionsForClassTypes.assertThat(expected).isNull();
	}

	@Test
	public void shouldNotLogInPlayer_invalidPassword() {
		Mockito.when(playerRepository.findPlayer(player.getUsername()))
				.thenReturn(Optional.of(player));

		Player expected = playerService.logIn(player.getUsername(), "1");

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("{{FAIL}} Incorrect password!");
		AssertionsForClassTypes.assertThat(expected).isNull();
	}


	@Test
	public void shouldGetBalancePlayer_successful() {
		Mockito.when(playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID())).thenReturn(100.0);

		playerService.displayPlayerBalance(player);

		Mockito.verify(playerRepository, Mockito.times(1))
				.findPlayerBalanceByPlayerID(player.getPlayerID());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Balance -- " + 100.0);
	}

}