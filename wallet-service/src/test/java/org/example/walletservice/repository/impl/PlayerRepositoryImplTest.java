package org.example.walletservice.repository.impl;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.exception.LiquibaseException;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.assertj.core.api.Assertions;
import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.enums.Operation;
import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@SpringBootTest
class PlayerRepositoryImplTest extends AbstractPostgreSQLContainer {
	private static PlayerRepository playerRepository;
	private static TransactionRepository transactionRepository;
	private static Player player;
	private static Transaction transaction;
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";
	private static final String TEST = "test";
	private static final String NOT_EXIST = "not_exist";
	private static final String TRANSACTION_TOKEN = "transaction_token";
	private static final BigDecimal BALANCE = BigDecimal.valueOf(100);

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
			Liquibase liquibase = new Liquibase(PATH_TO_CHANGELOG, new ClassLoaderResourceAccessor(),
					database);
			liquibase.update();
			playerRepository = new PlayerRepositoryImpl(jdbcTemplate);
			transactionRepository = new TransactionRepositoryImpl(jdbcTemplate);
		} catch (SQLException | LiquibaseException e) {
			e.printStackTrace();
		}
	}

	@BeforeEach
	void setUp() {
		player = new Player();
		player.setPlayerID(2);
		player.setUsername(TEST);
		player.setPassword(TEST);
		player.setRole(Role.USER);

		transaction = new Transaction();
		transaction.setToken(TRANSACTION_TOKEN);
		transaction.setOperation(Operation.CREDIT.name());
		transaction.setAmount(BigDecimal.ZERO);
		transaction.setPlayerID(player.getPlayerID());
	}

	@Test
	@DisplayName("Must return the player found")
	public void shouldFindPlayer() {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(ADMIN);

		AssertionsForClassTypes.assertThat(optionalPlayer).isNotEmpty();
	}

	@Test
	@DisplayName("Must return an empty player")
	public void shouldFindEmptyPlayer() {
		Optional<Player> optionalPlayer = playerRepository.findPlayer(NOT_EXIST);

		AssertionsForClassTypes.assertThat(optionalPlayer).isEmpty();
	}

	@Test
	@DisplayName("Must register a user")
	public void shouldRegistrationPlayer() {
		Player player = new Player();
		player.setUsername("t");
		player.setPassword("t");

		playerRepository.registrationPayer(player);

		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());
		Player expected = optionalPlayer.get();

		Assertions.assertThat(player.getUsername()).isEqualTo(expected.getUsername());
		Assertions.assertThat(player.getPassword()).isEqualTo(expected.getPassword());
	}

	@Test
	@DisplayName("Must return the player's balance by id")
	public void shouldGetBalanceByPlayerID() {
		Player newPlayer = new Player();
		newPlayer.setPlayerID(1);

		Player expectedPlayer = playerRepository.findPlayerBalance(newPlayer);

		AssertionsForClassTypes.assertThat(BigDecimal.ZERO).isEqualTo(expectedPlayer.getBalance());
	}

	@Test
	@DisplayName("Must get the player's new balance by their ID after the account is filled")
	public void shouldReceiveBalanceByPlayerIDAfterDepositing() {
		transactionRepository.creditOrDebit(transaction, BALANCE);

		Player expectedPlayer = playerRepository.findPlayerBalance(player);

		AssertionsForClassTypes.assertThat(BALANCE).isEqualTo(expectedPlayer.getBalance());
	}
}