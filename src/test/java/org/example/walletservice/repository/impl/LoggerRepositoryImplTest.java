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
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testcontainers.containers.PostgreSQLContainer;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

class LoggerRepositoryImplTest {
	private static LoggerRepository loggerRepository;
	private static PlayerRepository playerRepository;
	private static Player player;
	private static final String LOG_FORMAT = "--Operation: %s; \t--User: %s; \t--Status: %s.";
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";
	private static final String TEST = "test";
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
		loggerRepository = new LoggerRepositoryImpl(connectionProvider);
		playerRepository = new PlayerRepositoryImpl(connectionProvider);
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
				.role(Role.ADMIN).build();
	}

	@Test
	public void shouldReturnAllActivity() {
		//given
		playerRepository.registrationPayer(player);

		String firstRecord = String.format(LOG_FORMAT, Operation.REGISTRATION, player.getUsername(), Status.SUCCESSFUL);
		String secondRecord = String.format(LOG_FORMAT, Operation.CREDIT, ADMIN, Status.SUCCESSFUL);

		loggerRepository.recordAction(player.getPlayerID(), firstRecord);
		loggerRepository.recordAction(player.getPlayerID(), secondRecord);

		//when
		List<String> allLog = loggerRepository.findAllActivityRecords();

		//then
		AssertionsForClassTypes.assertThat(allLog).asList().contains(firstRecord, secondRecord);
	}

	@Test
	public void shouldRecordAction() {
		//given
		String playerActionRecord = String.format(LOG_FORMAT, Operation.DEBIT, ADMIN, Status.SUCCESSFUL);

		//when
		loggerRepository.recordAction(1, playerActionRecord);

		//then
		List<String> playerAction = loggerRepository.findActivityRecordsForPlayer(1);
		AssertionsForClassTypes.assertThat(playerAction).asList().contains(playerActionRecord);
	}

	@Test
	public void shouldReturnEmptyRecordAction() {
		//when
		List<String> recordAction = loggerRepository.findActivityRecordsForPlayer(13);

		//then
		AssertionsForClassTypes.assertThat(recordAction).asList().isEmpty();
	}

	@Test
	public void shouldReturnRecordActionForPlayer() {
		//given
		String firstRecord = String.format(LOG_FORMAT, Operation.REGISTRATION, ADMIN, Status.SUCCESSFUL);
		String secondRecord = String.format(LOG_FORMAT, Operation.CREDIT, ADMIN, Status.SUCCESSFUL);

		loggerRepository.recordAction(1, firstRecord);
		loggerRepository.recordAction(1, secondRecord);

		//when
		List<String> recordAction = loggerRepository.findActivityRecordsForPlayer(1);

		//then
		AssertionsForClassTypes.assertThat(recordAction).asList().contains(firstRecord, secondRecord);
	}
}