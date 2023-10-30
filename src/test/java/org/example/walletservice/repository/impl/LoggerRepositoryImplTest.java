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
import org.example.walletservice.model.entity.Log;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.junit.jupiter.api.*;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

@Disabled
class LoggerRepositoryImplTest extends AbstractPostgreSQLContainer {
	private static LoggerRepository loggerRepository;
	private static PlayerRepository playerRepository;
	private static Player player;
	private static final String LOG_FORMAT = "--Operation: %s; \t--User: %s; \t--Status: %s.";
	private static final String ADMIN = "admin";
	private static final String PATH_TO_CHANGELOG = "changelog/changelog.xml";
	private static final String TEST = "test";

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
			loggerRepository = new LoggerRepositoryImpl(jdbcTemplate);
			playerRepository = new PlayerRepositoryImpl(jdbcTemplate);
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
		player.setRole(Role.ADMIN);
	}

	@Test
	@DisplayName("Must return records all operations players")
	public void shouldReturnAllActivity() {
		playerRepository.registrationPayer(player);
		String firstRecord = String.format(LOG_FORMAT, Operation.REGISTRATION, player.getUsername(), Status.SUCCESSFUL);
		String secondRecord = String.format(LOG_FORMAT, Operation.CREDIT, ADMIN, Status.SUCCESSFUL);
		Log firstLog = new Log();
		firstLog.setLog(firstRecord);
		firstLog.setPlayerID(player.getPlayerID());
		Log secondLog = new Log();
		secondLog.setLog(secondRecord);
		secondLog.setPlayerID(player.getPlayerID());
		loggerRepository.recordAction(firstLog);
		loggerRepository.recordAction(secondLog);

		List<Log> allLog = loggerRepository.findAllActivityRecords();

		Assertions.assertThat(allLog)
				.extracting(Log::getLog)
				.contains(firstRecord, secondRecord);
	}

	@Test
	@DisplayName("Must record action player")
	public void shouldRecordAction() {
		String playerActionRecord = String.format(LOG_FORMAT, Operation.DEBIT, ADMIN, Status.SUCCESSFUL);
		Log log = new Log();
		log.setLog(playerActionRecord);
		log.setPlayerID(1);
		loggerRepository.recordAction(log);

		List<Log> playerAction = loggerRepository.findActivityRecordsForPlayer(1);
		AssertionsForClassTypes.assertThat(playerAction.get(playerAction.size() - 1).getLog())
				.contains(playerActionRecord);
	}

	@Test
	@DisplayName("Should return an empty list of player activity records")
	public void shouldReturnEmptyRecordAction() {
		List<Log> recordAction = loggerRepository.findActivityRecordsForPlayer(13);

		AssertionsForClassTypes.assertThat(recordAction).asList().isEmpty();
	}

	@Test
	@DisplayName("Must return all action of a particular user")
	public void shouldReturnRecordActionForPlayer() {
		String firstRecord = String.format(LOG_FORMAT, Operation.REGISTRATION, ADMIN, Status.SUCCESSFUL);
		String secondRecord = String.format(LOG_FORMAT, Operation.CREDIT, ADMIN, Status.SUCCESSFUL);
		Log firstLog = new Log();
		firstLog.setLog(firstRecord);
		firstLog.setPlayerID(1);
		Log secondLog = new Log();
		secondLog.setLog(secondRecord);
		secondLog.setPlayerID(1);

		loggerRepository.recordAction(firstLog);
		loggerRepository.recordAction(secondLog);

		List<Log> recordAction = loggerRepository.findActivityRecordsForPlayer(1);

		Assertions.assertThat(recordAction)
				.extracting(Log::getLog)
				.contains(firstRecord, secondRecord);
	}
}