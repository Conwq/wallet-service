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
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.service.enums.Operation;
import org.junit.jupiter.api.*;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class TransactionRepositoryImplTest {
	private static final String TRANSACTION_OUTPUT_FORMAT = "*****************-%s-*****************\n" +
			"\t-- Transaction number: %s\n" +
			"\t-- Your balance after transaction: %s\n" +
			"******************************************\n";
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private static final double BALANCE_PLAYER = 1000.0;
	private static PlayerRepository playerRepository;
	private static TransactionRepository transactionRepository;
	private static Player player;
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";
	static final PostgreSQLContainer<?> POSTGRESQL = new PostgreSQLContainer<>(
			"postgres:latest"
	);

	@BeforeAll
	static void beforeAll() {
		POSTGRESQL.start();

		ConnectionProvider connectionProvider = new ConnectionProvider(
				POSTGRESQL.getJdbcUrl(),
				POSTGRESQL.getUsername(),
				POSTGRESQL.getPassword()
		);

		try (Connection connection = connectionProvider.takeConnection()) {
			Database database = DatabaseFactory
					.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));

			Liquibase liquibase = new Liquibase(
					PATH_TO_CHANGELOG,
					new ClassLoaderResourceAccessor(),
					database
			);

			liquibase.update();
		} catch (SQLException | LiquibaseException e) {
			e.printStackTrace();
		}
		playerRepository = new PlayerRepositoryImpl(connectionProvider);
		transactionRepository = new TransactionRepositoryImpl(connectionProvider);
	}

	@AfterAll
	static void afterAll() {
		POSTGRESQL.stop();
	}

	@BeforeEach
	void setUp() {
		player = Player.builder().playerID(1)
				.username(ADMIN)
				.password(ADMIN)
				.role(Role.ADMIN).build();
	}


	@Test
	public void mustReturnFalseAfterValidatingToken() {
		boolean value = transactionRepository.checkTokenExistence(TRANSACTION_TOKEN);

		AssertionsForClassTypes.assertThat(value).isFalse();
	}

	@Test
	public void shouldChangePlayerBalanceAfterDepositingAndGetTransactionHistory() {
		transactionRepository.creditOrDebit(
				BALANCE_PLAYER,
				player.getPlayerID(),
				TRANSACTION_TOKEN,
				Operation.CREDIT
		);

		double playerBalance = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());
		List<String> playerTransactionHistory = transactionRepository
				.findPlayerTransactionalHistoryByPlayerID(player.getPlayerID());
		AssertionsForClassTypes.assertThat(playerBalance).isEqualTo(BALANCE_PLAYER);
		AssertionsForClassTypes.assertThat(playerTransactionHistory).asString().contains(
				String.format(TRANSACTION_OUTPUT_FORMAT, Operation.CREDIT, TRANSACTION_TOKEN, BALANCE_PLAYER)
		);
	}

	@Test
	public void mustReturnTrueAfterValidatingToken() {
		boolean value = transactionRepository.checkTokenExistence(TRANSACTION_TOKEN);

		AssertionsForClassTypes.assertThat(value).isTrue();
	}
}