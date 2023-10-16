package org.example.walletservice.container;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractPostgreSQLContainer {

	static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER;

	static {
		POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:latest");
		POSTGRESQL_CONTAINER.start();
	}
}
