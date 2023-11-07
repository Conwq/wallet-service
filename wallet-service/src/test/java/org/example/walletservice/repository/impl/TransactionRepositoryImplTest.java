package org.example.walletservice.repository.impl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.enums.Operation;
import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
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
		PGSimpleDataSource dataSource = new PGSimpleDataSource();
		dataSource.setUrl(POSTGRES.getJdbcUrl());
		dataSource.setUser(POSTGRES.getUsername());
		dataSource.setPassword(POSTGRES.getPassword());

		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);

		try (Connection connection = dataSource.getConnection()) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
					new JdbcConnection(connection));
			Liquibase liquibase = new Liquibase(PATH_TO_CHANGELOG, new ClassLoaderResourceAccessor(), database);
			liquibase.update();

			transactionRepository = new TransactionRepositoryImpl(jdbcTemplate);
			playerRepository = new PlayerRepositoryImpl(jdbcTemplate);

		} catch (SQLException | LiquibaseException e) {
			e.printStackTrace();
		}
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

		Player playerExpected = playerRepository.findPlayerBalance(player);

		List<Transaction> playerTransactionHistory = transactionRepository
				.findPlayerTransactionalHistory(player);

		System.out.println(playerTransactionHistory);
		System.out.println(List.of(transaction));

		AssertionsForClassTypes.assertThat(playerExpected.getBalance()).isEqualTo(BALANCE_PLAYER);
	}

	@Test
	@DisplayName("After validating the token, it should return a positive value")
	public void shouldReturnTrueAfterValidatingToken() {
		boolean value = transactionRepository.checkTokenExistence(TRANSACTION_TOKEN);

		AssertionsForClassTypes.assertThat(value).isTrue();
	}
}