package org.example.walletservice;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.in.MainMenu;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.sql.Connection;
import java.sql.Statement;

/**
 * The main class for the Wallet Service application.
 * Starts the main menu of the application.
 */
public final class WalletServiceApplication {
	private static final ApplicationContextHolder context = ApplicationContextHolder.getInstance();
	private static final ConnectionProvider connectionProvider = context.getConnectionProvider();

	static {
		performingDatabaseMigration();
	}

	public static void main(String... args) {

		MainMenu mainMenu = context.getMainMenu();
		mainMenu.start();
	}

	/**
	 * Performing a Database Migration
	 */
	private static void performingDatabaseMigration(){
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

			Liquibase liquibase = new Liquibase("changelog/changelog.xml", new ClassLoaderResourceAccessor(),
					database);
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