package org.example.walletservice.context;

import com.fasterxml.jackson.databind.ObjectMapper;
import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.walletservice.in.command.CommandProvider;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.mapper.LogMapper;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.model.mapper.TransactionMapper;
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
	private static final ApplicationContextHolder instance = new ApplicationContextHolder();

	static {
		performingDatabaseMigration();
	}

	private static final String URL = "url";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PROPERTIES_NAME = "liquibase";

	private final PlayerMapper playerMapper = PlayerMapper.instance;
	private final TransactionMapper transactionMapper = TransactionMapper.instance;
	private final LogMapper logMapper = LogMapper.instance;
	private final CommandProvider commandProvider = new CommandProvider();
	private final ObjectMapper objectMapper = new ObjectMapper();
	private final DBResourceManager liquibaseResourceManager = new DBResourceManager(PROPERTIES_NAME);
	private final JwtService jwtService = new JwtService();
	private final ConnectionProvider connectionProvider = new ConnectionProvider(
			liquibaseResourceManager.getValue(URL),
			liquibaseResourceManager.getValue(USERNAME),
			liquibaseResourceManager.getValue(PASSWORD));

	private final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionProvider);
	private final TransactionRepository transactionRepository = new TransactionRepositoryImpl(connectionProvider);
	private final LoggerRepository loggerRepository = new LoggerRepositoryImpl(connectionProvider);

	private final LoggerService loggerService = new LoggerServiceImpl(loggerRepository, playerRepository,
			logMapper, playerMapper);
	private final TransactionService transactionService = new TransactionServiceImpl(transactionRepository,
			playerRepository, transactionMapper, playerMapper);
	private final PlayerService playerService = new PlayerServiceImpl(playerRepository, loggerService, playerMapper);

	private ApplicationContextHolder() {
	}

	public static ApplicationContextHolder getInstance() {
		return instance;
	}

	public JwtService getJwtService() {
		return jwtService;
	}

	public DBResourceManager getLiquibaseResourceManager() {
		return liquibaseResourceManager;
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

	public PlayerMapper getPlayerMapper() {
		return playerMapper;
	}

	public TransactionMapper getTransactionMapper() {
		return transactionMapper;
	}

	public LogMapper getLogMapper() {
		return logMapper;
	}

	private static void performingDatabaseMigration() {
		final String queryCreateSchema = "CREATE SCHEMA IF NOT EXISTS migration";
		final String schemaName = "migration";
		final String pathToChangelog = "changelog/changelog.xml";
		final String defaultSchemaName = "wallet_service";

		ConnectionProvider connectionProvider = instance.getConnectionProvider();
		Connection connection = null;
		Statement statement = null;
		try {
			connection = connectionProvider.takeConnection();
			statement = connection.createStatement();
			statement.executeUpdate(queryCreateSchema);
			connection.commit();

			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setLiquibaseSchemaName(schemaName);

			Liquibase liquibase =
					new Liquibase(pathToChangelog, new ClassLoaderResourceAccessor(), database);
			liquibase.getDatabase().setDefaultSchemaName(defaultSchemaName);
			liquibase.update();

		} catch (Exception e) {
			connectionProvider.rollbackCommit(connection);

		} finally {
			connectionProvider.closeConnection(connection, statement);
		}
	}
}
