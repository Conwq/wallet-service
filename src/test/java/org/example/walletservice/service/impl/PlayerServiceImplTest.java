package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.logger.TransactionLog;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.*;

class PlayerServiceImplTest {
	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private final Scanner scanner = Mockito.mock(Scanner.class);
	private final TransactionLog transactionLog = Mockito.mock(TransactionLog.class);
	private PlayerService playerService;
	private static final String BALANCE = "10000";
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		playerService = new PlayerServiceImpl(playerRepository, scanner, transactionLog);

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

		playerService.registrationPlayer(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		Mockito.verify(playerRepository, Mockito.times(1)).registrationPayer(player);
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
				.thenReturn(Optional.of(new Player(player.getUsername(), "test", Role.USER)));

		Player expected = playerService.logIn(player.getUsername(), player.getPassword());

		Mockito.verify(playerRepository, Mockito.times(1)).findPlayer(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("{{FAIL}} Incorrect password!");
		AssertionsForClassTypes.assertThat(expected).isNull();
	}

	@Test
	public void shouldGetBalancePlayer_successful() {
		Mockito.when(playerRepository.getPlayerBalance(player)).thenReturn(BALANCE);

		playerService.showPlayerBalance(player);

		Mockito.verify(playerRepository, Mockito.times(1)).getPlayerBalance(player);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Balance -- " + BALANCE);
	}

	@Test
	public void shouldCredit_successful() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(playerRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);

		playerService.credit(player);

		Mockito.verify(playerRepository, Mockito.times(1))
				.credit(AMOUNT, player, TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Credit successfully.");
	}

	@Test
	public void shouldNotCredit_transactionNumberNotUnique() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(playerRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);

		playerService.credit(player);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
		Mockito.verify(playerRepository, Mockito.never()).credit(AMOUNT, player, TRANSACTIONAL_TOKEN);
	}
	
	@Test
	public void shouldDebit_successful() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(playerRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);

		player.setBalance(AMOUNT);
		playerService.debit(player);

		Mockito.verify(playerRepository, Mockito.times(1))
				.debit(AMOUNT, player, TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Debit successfully.");
	}

	@Test
	public void shouldDebit_transactionNumberNotUnique() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(playerRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);

		player.setBalance(AMOUNT);
		playerService.debit(player);

		Mockito.verify(playerRepository, Mockito.never()).debit(AMOUNT, player, TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
	}
	
	
	@Test
	public void shouldGetPlayerTransactionalHistory_successful() {
		Map<String, String> testTransactionHistory = new HashMap<>(){{
			put("1", "Transaction #1");
			put("2", "Transaction #2");
		}};
		
		Mockito.when(playerRepository.getPlayerTransactionalHistory(player.getUsername()))
				.thenReturn(testTransactionHistory);

		playerService.getPlayerTransactionalHistory(player);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transaction #1", "Transaction #2");
	}

	@Test
	public void shouldGetPlayerTransactionalHistory_emptyMap(){
		Map<String, String> testTransactionHistory = new HashMap<>();

		Mockito.when(playerRepository.getPlayerTransactionalHistory(player.getUsername()))
				.thenReturn(testTransactionHistory);

		playerService.getPlayerTransactionalHistory(player);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transactions is empty.");
	}

	@Test
	public void shouldShowAllLogs_successful(){
		List<String> allLogs = new ArrayList<>(){{
			add("log #1");
			add("log #2");
			add("log #3");
		}};

		Mockito.when(transactionLog.getAllTransactionRecords()).thenReturn(allLogs);

		playerService.showAllLogs(player);

		Mockito.verify(transactionLog, Mockito.times(1)).getAllTransactionRecords();
		AssertionsForClassTypes.assertThat(outputStream.toString())
						.contains("log #1", "log #2", "log #3");
	}

	@Test
	public void shouldNotShowAllLogs_logsIsEmpty(){
		Mockito.when(transactionLog.getAllTransactionRecords()).thenReturn(new ArrayList<>());

		playerService.showAllLogs(player);

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("*No logs*");
	}

	@Test
	public void shouldShowPlayerLogs_successful() {
		Mockito.when(transactionLog.getLogsForPlayer(player.getUsername()))
				.thenReturn(new ArrayList<>(List.of("Transact #1", "Transact #2")));

		playerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Transact #1", "Transact #2");
	}

	@Test
	public void shouldNotShowPlayerLogs_playerNotFound() {
		Mockito.when(transactionLog.getLogsForPlayer(player.getUsername())).thenReturn(null);

		playerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*Player %s not found*", player.getUsername()));
	}

	@Test
	public void shouldNotShowPlayerLogs_playerLogsIsEmpty() {
		Mockito.when(transactionLog.getLogsForPlayer(player.getUsername())).thenReturn(new ArrayList<>());

		playerService.showLogsByUsername(player, player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains(String.format("*No logs for player %s*", player.getUsername()));
	}
}