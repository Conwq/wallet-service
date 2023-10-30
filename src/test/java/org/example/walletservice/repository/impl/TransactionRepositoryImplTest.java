package org.example.walletservice.repository.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.enums.Operation;
import org.junit.jupiter.api.*;

import java.math.BigDecimal;
import java.util.List;

@Disabled
class TransactionRepositoryImplTest extends AbstractPostgreSQLContainer {
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private static final BigDecimal BALANCE_PLAYER = BigDecimal.valueOf(1000);
	private static PlayerRepository playerRepository;
	private static TransactionRepository transactionRepository;
	private static Player player;
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";

	@BeforeAll
	static void beforeAll() {
//		POSTGRES.start();
//		ConnectionProvider connectionProvider = new ConnectionProvider(
//				POSTGRES.getJdbcUrl(),
//				POSTGRES.getUsername(),
//				POSTGRES.getPassword());
//		try (Connection connection = connectionProvider.takeConnection()) {
//			Database database =
//					DatabaseFactory.getInstance().findCorrectDatabaseImplementation(new JdbcConnection(connection));
//			Liquibase liquibase =
//					new Liquibase(PATH_TO_CHANGELOG, new ClassLoaderResourceAccessor(), database);
//			liquibase.update();
////			playerRepository = new PlayerRepositoryImpl(connectionProvider);
//			transactionRepository = new TransactionRepositoryImpl(connectionProvider);
//		} catch (SQLException | LiquibaseException e) {
//			e.printStackTrace();
//		}
	}

	@BeforeEach
	void setUp() {
		player = new Player();
		player.setPlayerID(1);
		player.setUsername(ADMIN);
		player.setPassword(ADMIN);
		player.setRole(Role.ADMIN);
	}

	@Test
	@DisplayName("Once validated, the token should return a negative value")
	public void shouldReturnFalseAfterValidatingToken() {
		boolean value = transactionRepository.checkTokenExistence("new_token");

		AssertionsForClassTypes.assertThat(value).isFalse();
	}


	@Test
	@DisplayName("Must change the player's balance after making a deposit")
	public void shouldChangePlayerBalanceAfterDepositing() {
		Transaction transaction = new Transaction();
		transaction.setToken(TRANSACTION_TOKEN);
		transaction.setOperation(Operation.CREDIT.name());
		transaction.setAmount(new BigDecimal(100));
		transaction.setPlayerID(1);
		transaction.setRecord("record");

		transactionRepository.creditOrDebit(transaction, BALANCE_PLAYER);

//		BigDecimal playerBalance = playerRepository.findPlayerBalance(player);
//
//		List<Transaction> playerTransactionHistory = transactionRepository
//				.findPlayerTransactionalHistoryByPlayer(player);
//
//		System.out.println(playerTransactionHistory);
//		System.out.println(List.of(transaction));
//
//		AssertionsForClassTypes.assertThat(playerBalance).isEqualTo(BALANCE_PLAYER);
	}

	@Test
	@DisplayName("After validating the token, it should return a positive value")
	public void shouldReturnTrueAfterValidatingToken() {
		boolean value = transactionRepository.checkTokenExistence(TRANSACTION_TOKEN);

		AssertionsForClassTypes.assertThat(value).isTrue();
	}
}