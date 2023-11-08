package org.example.walletservice.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Configuration
public class LiquibaseMigration {
	private final DataSource dataSource;
	@Value("${spring.liquibase.liquibase-schema}")
	private String liquibaseSchema;
	@Value("${spring.liquibase.default-schema}")
	private String defaultSchema;
	@Value("${spring.liquibase.change-log}")
	private String changeLog;

	@Autowired
	public LiquibaseMigration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@EventListener(ApplicationReadyEvent.class)
	public void performingDatabaseMigration() {
		final String queryCreateSchema = "CREATE SCHEMA IF NOT EXISTS migration";

		try (Connection connection = dataSource.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.executeUpdate(queryCreateSchema);

			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setLiquibaseSchemaName(liquibaseSchema);
			database.setDefaultSchemaName(defaultSchema);

			Liquibase liquibase = new Liquibase(changeLog, new ClassLoaderResourceAccessor(), database);
			liquibase.update();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}