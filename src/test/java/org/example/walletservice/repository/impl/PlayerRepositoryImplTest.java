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
	private static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
			"postgres:latest"
	);
	private static PlayerRepository playerRepository;
	private static TransactionRepository transactionRepository;
	private static Player player;

	@BeforeAll
	static void beforeAll() {
		postgres.start();

		ConnectionProvider connectionProvider = new ConnectionProvider(
				postgres.getJdbcUrl(),
				postgres.getUsername(),
				postgres.getUsername()
		);

		try (Connection connection = connectionProvider.takeConnection()) {
			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection)
					);

			Liquibase liquibase = new Liquibase("changelog/changelog.xml",
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
		postgres.stop();
	}

	@BeforeEach
	void setUp() {
		ConnectionProvider connectionProvider = new ConnectionProvider(
				postgres.getJdbcUrl(),
				postgres.getUsername(),
				postgres.getUsername()
		);
		playerRepository = new PlayerRepositoryImpl(connectionProvider);

		player = Player.builder().playerID(2)
				.username("e")
				.password("1313")
				.role(Role.USER).build();
	}

	@Test
	public void shouldFindPlayer_returnPlayer() {
		//when
		Optional<Player> optionalPlayer = playerRepository.findPlayer("admin");

		//then
		AssertionsForClassTypes.assertThat(optionalPlayer).isNotEmpty();
	}

	@Test
	public void shouldFindPlayer_returnEmptyPlayer() {
		//when
		Optional<Player> optionalPlayer = playerRepository.findPlayer("testing");

		//then
		AssertionsForClassTypes.assertThat(optionalPlayer).isEmpty();
	}

	@Test
	public void shouldRegistrationPlayer_successful() {
		//when
		int expectedPlayerID = playerRepository.registrationPayer(player);

		//then
		Optional<Player> optionalPlayer = playerRepository.findPlayer(player.getUsername());
		Player expected = optionalPlayer.get();
		AssertionsForClassTypes.assertThat(player).isEqualTo(expected);
		AssertionsForClassTypes.assertThat(2).isEqualTo(expectedPlayerID);
	}

	@Test
	public void shouldGetBalanceByPlayerID() {
		//when
		double expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayerID(1);
		//then
		AssertionsForClassTypes.assertThat(0.0).isEqualTo(expectedBalancePlayer);
	}

	@Test
	public void shouldReceiveBalanceByPlayerIDAfterDepositing(){
		//given
		transactionRepository.creditOrDebit(100.0, player.getPlayerID(), "unique_token", Operation.CREDIT);

		//when
		double expectedBalancePlayer = playerRepository.findPlayerBalanceByPlayerID(player.getPlayerID());

		//then
		AssertionsForClassTypes.assertThat(100.0).isEqualTo(expectedBalancePlayer);
	}
}