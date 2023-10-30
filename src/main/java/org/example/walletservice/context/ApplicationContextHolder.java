package org.example.walletservice.context;

import org.example.walletservice.jwt.JwtService;

/**
 * Singleton class responsible for managing the application context.
 */
public class ApplicationContextHolder {
	private static final ApplicationContextHolder instance = new ApplicationContextHolder();

	static {
	}

	private static final String URL = "url";
	private static final String USERNAME = "username";
	private static final String PASSWORD = "password";
	private static final String PROPERTIES_NAME = "liquibase";

//	private final PlayerMapper playerMapper = PlayerMapper.instance;
//	private final TransactionMapper transactionMapper = TransactionMapper.instance;
//	private final LogMapper logMapper = LogMapper.instance;
	private final JwtService jwtService = new JwtService();


//	private final PlayerRepository playerRepository = new PlayerRepositoryImpl(connectionProvider);


//	private final LoggerService loggerService = new LoggerServiceImpl(loggerRepository, playerRepository,
//			logMapper, playerMapper);
//	private final TransactionService transactionService = new TransactionServiceImpl(transactionRepository,
//			playerRepository, transactionMapper, playerMapper);
//	private final PlayerService playerService = new PlayerServiceImpl(playerRepository, loggerService, playerMapper);

	private ApplicationContextHolder() {
	}

	public static ApplicationContextHolder getInstance() {
		return instance;
	}

	public JwtService getJwtService() {
		return jwtService;
	}


//	public PlayerRepository getPlayerRepository() {
//		return playerRepository;
//	}


//	public LoggerService getLoggerService() {
//		return loggerService;
//	}
//
//	public TransactionService getTransactionService() {
//		return transactionService;
//	}

//	public PlayerService getPlayerService() {
//		return playerService;
//	}


//	public PlayerMapper getPlayerMapper() {
//		return playerMapper;
//	}
//
//	public TransactionMapper getTransactionMapper() {
//		return transactionMapper;
//	}
//
//	public LogMapper getLogMapper() {
//		return logMapper;
//	}

//	private static void performingDatabaseMigration() {
//		final String queryCreateSchema = "CREATE SCHEMA IF NOT EXISTS migration";
//		final String schemaName = "migration";
//		final String pathToChangelog = "changelog/changelog.xml";
//		final String defaultSchemaName = "wallet_service";
//
//		ConnectionProvider connectionProvider = instance.getConnectionProvider();
//		Connection connection = null;
//		Statement statement = null;
//		try {
//			connection = connectionProvider.takeConnection();
//			statement = connection.createStatement();
//			statement.executeUpdate(queryCreateSchema);
//			connection.commit();
//
//			Database database = DatabaseFactory.getInstance()
//					.findCorrectDatabaseImplementation(new JdbcConnection(connection));
//			database.setLiquibaseSchemaName(schemaName);
//
//			Liquibase liquibase =
//					new Liquibase(pathToChangelog, new ClassLoaderResourceAccessor(), database);
//			liquibase.getDatabase().setDefaultSchemaName(defaultSchemaName);
//			liquibase.update();
//
//		} catch (Exception e) {
//			connectionProvider.rollbackCommit(connection);
//
//		} finally {
//			connectionProvider.closeConnection(connection, statement);
//		}
//	}
}
