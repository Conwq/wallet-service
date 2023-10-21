package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Disabled
class TransactionServiceTest {
	private final LoggerServiceImpl loggerService = Mockito.mock(LoggerServiceImpl.class);
	private final TransactionRepository transactionRepository = Mockito.mock(TransactionRepository.class);
	private final PlayerRepository playerRepository = Mockito.mock(PlayerRepository.class);
	private TransactionService transactionService;
	private static final BigDecimal AMOUNT = BigDecimal.valueOf(100.0);
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private final PrintStream origOut = System.out;
	private final InputStream origIn = System.in;
	private ByteArrayOutputStream outputStream;

	@BeforeEach
	public void setUp() {
		transactionService = new TransactionServiceImpl(
				loggerService,
				transactionRepository,
				playerRepository
		);

//		player = Player.builder()
//				.playerID(1)
//				.username("user123")
//				.password("1313")
//				.role(Role.USER).build();

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
	public void shouldCredit_successful() {
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);
//		Mockito.when(playerRepository.findPlayerBalanceByPlayer(player.getPlayerID()))
//						.thenReturn(AMOUNT);

//		transactionService.credit(player, AMOUNT, TRANSACTIONAL_TOKEN);

		Mockito.verify(transactionRepository, Mockito.times(1))
				.creditOrDebit(Mockito.any(Transaction.class), Mockito.any(BigDecimal.class));
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Credit successfully.");
	}

	@Test
	public void shouldNotCredit_transactionNumberNotUnique() {
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);

//		transactionService.credit(player, AMOUNT, TRANSACTIONAL_TOKEN);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
		Mockito.verify(transactionRepository, Mockito.never())
				.creditOrDebit(Mockito.any(Transaction.class), Mockito.any(BigDecimal.class));
	}

	@Test
	public void shouldDebit_successful() {
		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);
//		Mockito.when(playerRepository.findPlayerBalanceByPlayer(player.getPlayerID()))
//				.thenReturn(BigDecimal.valueOf(200));

//		transactionService.debit(player, AMOUNT, TRANSACTIONAL_TOKEN);

		Mockito.verify(transactionRepository, Mockito.times(1))
				.creditOrDebit(Mockito.any(Transaction.class), Mockito.any(BigDecimal.class));
		AssertionsForClassTypes.assertThat(outputStream.toString()).contains("Debit successfully.");
	}

	@Test
	public void shouldDebit_transactionNumberNotUnique() {
//		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(true);
//
//		transactionService.debit(player, AMOUNT, TRANSACTIONAL_TOKEN);

		Mockito.verify(transactionRepository, Mockito.never())
				.creditOrDebit(Mockito.any(Transaction.class), Mockito.any(BigDecimal.class));
		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("{{FAIL}} A transaction with this number already exists!");
	}


	@Test
	public void shouldGetPlayerTransactionalHistory_successful() {
		List<String> testTransactionHistory = new ArrayList<>() {{
			add("Transaction #1");
			add("Transaction #2");
		}};

//		Mockito.when(transactionRepository.findPlayerTransactionalHistoryByPlayer(player.getPlayerID()))
//				.thenReturn(testTransactionHistory);

//		transactionService.getPlayerTransactionalHistory(player);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transaction #1", "Transaction #2");
	}

	@Test
	public void shouldGetPlayerTransactionalHistory_emptyMap() {
		List<String> testTransactionHistory = new ArrayList<>();

//		Mockito.when(transactionRepository.findPlayerTransactionalHistoryByPlayer(player.getPlayerID()))
//				.thenReturn(testTransactionHistory);

//		transactionService.getPlayerTransactionalHistory(player);

		AssertionsForClassTypes.assertThat(outputStream.toString())
				.contains("Transactions is empty.");
	}
}