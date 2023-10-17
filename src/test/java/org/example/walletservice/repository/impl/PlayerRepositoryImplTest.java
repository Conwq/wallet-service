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
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

class PlayerRepositoryImplTest {
	private static PlayerRepository playerRepository;
	private static TransactionRepository transactionRepository;
	private static Player player;
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";
	private static final String TEST = "test";
	private static final String NOT_EXIST = "not_exist";
	private static final String TRANSACTION_TOKEN = "transaction_token";
	static final PostgreSQLContainer<?> POSTGRESQL = new PostgreSQLContainer<>(
			"postgres:latest"
	);

	@BeforeAll
	static void beforeAll() {
		POSTGRESQL.start();

		ConnectionProvider connectionProvider = new ConnectionProvider(
				POSTGRESQL.getJdbcUrl(),
				POSTGRESQL.getUsername(),
				POSTGRESQL.getUsername()
		);
		try (Connection connection = connectionProvider.takeConnection()) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
					new JdbcConnection(connection)
			);

			Liquibase liquibase = new Liquibase(PATH_TO_CHANGELOG,
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
		player = Player.builder().playerID(2)
				.username(TEST)
				.password(TEST)
				.role(Role.USER).build();
	}

	@Test
	public void shouldFindPlayer_returnPlayer() {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(ADMIN);

		AssertionsForClassTypes.assertThat(optionalPlayer).isNotEmpty();
	}

	@Test
	public void shouldFindPlayer_returnEmptyPlayer() {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(NOT_EXIST);

		AssertionsForClassTypes.assertThat(optionalPlayer).isEmpty();
	}

	@Test
	public void shouldRegistrationPlayer_successful() {
		int expectedPlayerID = playerRepository.registrationPayer(player);

		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());
		Player expected = optionalPlayer.get();
		AssertionsForClassTypes.assertThat(player).isEqualTo(expected);
		AssertionsForClassTypes.assertThat(2).isEqualTo(expectedPlayerID);
	}

	@Test
	public void shouldGetBalanceByPlayerID() {
		double expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayerID(1);

		AssertionsForClassTypes.assertThat(0.0).isEqualTo(expectedBalancePlayer);
	}

	@Test
	public void shouldReceiveBalanceByPlayerIDAfterDepositing() {
		transactionRepository.creditOrDebit(
				100.0,
				player.getPlayerID(),
				TRANSACTION_TOKEN,
				Operation.CREDIT
		);

		double expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());

		AssertionsForClassTypes.assertThat(100.0).isEqualTo(expectedBalancePlayer);
	}
}