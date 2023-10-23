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
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.service.enums.Operation;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

@Disabled
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
		ConnectionProvider connectionProvider = new ConnectionProvider(
				POSTGRES.getJdbcUrl(),
				POSTGRES.getUsername(),
				POSTGRES.getPassword());
		try (Connection connection = connectionProvider.takeConnection()) {
			Database database = DatabaseFactory.getInstance().findCorrectDatabaseImplementation(
					new JdbcConnection(connection));
			Liquibase liquibase = new Liquibase(PATH_TO_CHANGELOG, new ClassLoaderResourceAccessor(),
					database);
			liquibase.update();
			playerRepository = new PlayerRepositoryImpl(connectionProvider);
			transactionRepository = new TransactionRepositoryImpl(connectionProvider);
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
		playerRepository.registrationPayer(player);

		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());
		Player expected = optionalPlayer.get();
		AssertionsForClassTypes.assertThat(player).isEqualTo(expected);
	}

	@Test
	public void shouldGetBalanceByPlayerID() {
		Player newPlayer = new Player();
		newPlayer.setPlayerID(1);

		BigDecimal expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayer(newPlayer);

		AssertionsForClassTypes.assertThat(BigDecimal.ZERO).isEqualTo(expectedBalancePlayer);
	}

	@Test
	public void shouldReceiveBalanceByPlayerIDAfterDepositing() {
		transactionRepository.creditOrDebit(transaction, BALANCE);

		BigDecimal expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayer(player);

		AssertionsForClassTypes.assertThat(BALANCE).isEqualTo(expectedBalancePlayer);
	}
}