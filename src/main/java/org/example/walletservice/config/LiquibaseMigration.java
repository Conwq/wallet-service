package org.example.walletservice.config;

import liquibase.Liquibase;
import liquibase.database.Database;
import liquibase.database.DatabaseFactory;
import liquibase.database.jvm.JdbcConnection;
import liquibase.resource.ClassLoaderResourceAccessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.annotation.PropertySources;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;

@Component
@PropertySources({
		@PropertySource(value = "classpath:application.yml", factory = YamlProperty.class)
})
public class LiquibaseMigration {
	private final DataSource dataSource;
	@Value("${liquibase.default-schema}")
	private String defaultSchema;
	@Value("${liquibase.schema-migration}")
	private String schemaMigration;
	@Value("${liquibase.change-log}")
	private String changelogFile;

	@Autowired
	public LiquibaseMigration(DataSource dataSource) {
		this.dataSource = dataSource;
	}

	@PostConstruct
	public void performingDatabaseMigration() {
		final String queryCreateSchema = "CREATE SCHEMA IF NOT EXISTS migration";

		try (Connection connection = dataSource.getConnection();
			 Statement statement = connection.createStatement()) {
			statement.executeUpdate(queryCreateSchema);

			Database database = DatabaseFactory.getInstance()
					.findCorrectDatabaseImplementation(new JdbcConnection(connection));
			database.setLiquibaseSchemaName(schemaMigration);

			Liquibase liquibase =
					new Liquibase(changelogFile, new ClassLoaderResourceAccessor(), database);
			liquibase.getDatabase().setDefaultSchemaName(defaultSchema);
			liquibase.update();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}