package org.example.walletservice.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.model.mapper.PlayerMapperImpl;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.impl.LoggerRepositoryImpl;
import org.example.walletservice.repository.impl.PlayerRepositoryImpl;
import org.example.walletservice.repository.impl.TransactionRepositoryImpl;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.repository.manager.DBResourceManager;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.impl.LoggerServiceImpl;
import org.example.walletservice.service.impl.PlayerServiceImpl;
import org.example.walletservice.service.impl.TransactionServiceImpl;

import java.sql.Connection;
import java.sql.Statement;

/**
 * Singleton class responsible for managing the application context.
 */
public class ApplicationContextHolder {
	private static ApplicationContextHolder instance = new ApplicationContextHolder();

	static {
		performingDatabaseMigration();
	}

	private static final String URL = "url";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";

	final PlayerMapper playerMapper = new PlayerMapperImpl();
	final CommandProvider commandProvider = new CommandProvider();
	final ObjectMapper objectMapper = new ObjectMapper();
	final DBResourceManager resourceManager = new DBResourceManager();
	final ConnectionProvider connectionProvider = new ConnectionProvider(
			resourceManager.getValue(URL),
			resourceManager.getValue(USERNAME),
			resourceManager.getValue(PASSWORD));

	final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionProvider);
	final TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionProvider);
	final LoggerRepository loggerRepository = new LoggerRepositoryImpl(connectionProvider);

	final LoggerService loggerService = new LoggerServiceImpl(loggerRepository, playerRepository);
	final TransactionService transactionService = new TransactionServiceImpl(
			loggerService,
			transactionRepository,
			playerRepository);
	final PlayerService playerService = new PlayerServiceImpl(playerRepository, loggerService, playerMapper);

	private ApplicationContextHolder() {
	}

	public static ApplicationContextHolder getInstance() {
		if (instance == null) {
			instance = new ApplicationContextHolder();
		}
		return instance;
	}

	public DBResourceManager getResourceManager() {
		return resourceManager;
	}

	public PlayerRepository getPlayerRepository() {
		return playerRepository;
	}

	public TransactionRepository getTransactionRepository() {
		return transactionRepository;
	}

	public CommandProvider getCommandProvider() {
		return commandProvider;
	}

	public LoggerRepository getLoggerRepository() {
		return loggerRepository;
	}

	public LoggerService getLoggerService() {
		return loggerService;
	}

	public TransactionService getTransactionService() {
		return transactionService;
	}

	public PlayerService getPlayerService() {
		return playerService;
	}

	public ConnectionProvider getConnectionProvider() {
		return connectionProvider;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	private static void performingDatabaseMigration(){
		ConnectionProvider connectionProvider = instance.getConnectionProvider();
		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionProvider.takeConnection();
			statement = connection.createStatement();
			statement.executeUpdate("CREATE SCHEMA IF NOT EXISTS migration");
			connection.commit();

			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setLiquibaseSchemaName("migration");

			Liquibase liquibase = new Liquibase("changelog/changelog.xml",
					new ClassLoaderResourceAccessor(), database);
			liquibase.getDatabase().setDefaultSchemaName("wallet_service");
			liquibase.update();
		} catch (Exception e) {
			connectionProvider.rollbackCommit(connection);
			e.printStackTrace();
		} finally {
			connectionProvider.closeConnection(connection, statement);
		}
	}
}
