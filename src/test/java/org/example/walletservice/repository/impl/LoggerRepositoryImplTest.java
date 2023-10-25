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
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

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
			loggerRepository = new LoggerRepositoryImpl(connectionProvider);
			playerRepository = new PlayerRepositoryImpl(connectionProvider);
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
	public void shouldReturnEmptyRecordAction() {
		List<Log> recordAction = loggerRepository.findActivityRecordsForPlayer(13);

		AssertionsForClassTypes.assertThat(recordAction).asList().isEmpty();
	}

	@Test
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