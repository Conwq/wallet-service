package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.util.Cleaner;
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
import java.util.Scanner;

class TransactionServiceTest {
	private final Scanner scanner = Mockito.mock(Scanner.class);
	private final PlayerActionLoggerServiceImpl PlayerActionLoggerServiceImpl = Mockito.mock(PlayerActionLoggerServiceImpl.class);
	private final TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
	private final Cleaner cleaner = Mockito.mock(Cleaner.class);
	private TransactionService transactionService;
	private static final double BALANCE = 10000;
	private static final double AMOUNT = 100.0;
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		transactionService = new TransactionServiceImpl(scanner, PlayerActionLoggerServiceImpl, cleaner, transactionRepository);

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
	public void tearDown() {
		System.setOut(origOut);
		System.setIn(origIn);
	}

	@Test
	public void shouldGetBalancePlayer_successful() {
		Mockito.when(transactionRepository.findPlayerBalanceByUsername(player.getUsername())).thenReturn(BALANCE);

		transactionService.displayPlayerBalance(player.getUsername());

		Mockito.verify(transactionRepository, Mockito.times(1))
				.findPlayerBalanceByUsername(player.getUsername());
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Balance -- " + BALANCE);
	}

	@Test
	public void shouldCredit_successful() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);

		transactionService.credit(player.getUsername());

		Mockito.verify(transactionRepository, Mockito.times(1))
				.credit(AMOUNT, player.getUsername(), TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Credit successfully.");
	}

	@Test
	public void shouldNotCredit_transactionNumberNotUnique() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);

		transactionService.credit(player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
		Mockito.verify(transactionRepository, Mockito.never()).credit(AMOUNT, player.getUsername(), TRANSACTIONAL_TOKEN);
	}

	@Test
	public void shouldDebit_successful() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);
		Mockito.when(transactionRepository.findPlayerBalanceByUsername(player.getUsername())).thenReturn(200.0);

		player.setBalance(AMOUNT);
		transactionService.debit(player.getUsername());

		Mockito.verify(transactionRepository, Mockito.times(1))
				.debit(AMOUNT, player.getUsername(), TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Debit successfully.");
	}

	@Test
	public void shouldDebit_transactionNumberNotUnique() {
		Mockito.when(scanner.hasNextDouble()).thenReturn(true);
		Mockito.when(scanner.nextDouble()).thenReturn(AMOUNT);
		Mockito.when(scanner.nextLine()).thenReturn(TRANSACTIONAL_TOKEN);
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);

		player.setBalance(AMOUNT);
		transactionService.debit(player.getUsername());

		Mockito.verify(transactionRepository, Mockito.never()).debit(AMOUNT, player.getUsername(), TRANSACTIONAL_TOKEN);
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
	}


	@Test
	public void shouldGetPlayerTransactionalHistory_successful() {
		List<String> testTransactionHistory = new ArrayList<>() {{
			add("Transaction #1");
			add("Transaction #2");
		}};

		Mockito.when(transactionRepository.findPlayerTransactionalHistoryByUsername(player.getUsername()))
				.thenReturn(testTransactionHistory);

		transactionService.displayPlayerTransactionalHistoryByUsername(player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transaction #1", "Transaction #2");
	}

	@Test
	public void shouldGetPlayerTransactionalHistory_emptyMap() {
		List<String> testTransactionHistory = new ArrayList<>();

		Mockito.when(transactionRepository.findPlayerTransactionalHistoryByUsername(player.getUsername()))
				.thenReturn(testTransactionHistory);

		transactionService.displayPlayerTransactionalHistoryByUsername(player.getUsername());

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transactions is empty.");
	}
}