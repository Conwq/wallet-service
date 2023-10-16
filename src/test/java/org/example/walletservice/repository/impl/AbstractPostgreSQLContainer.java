package org.example.walletservice.repository.impl;

import org.testcontainers.containers.PostgreSQLContainer;

public class AbstractPostgreSQLContainer {

	static final PostgreSQLContainer<?> POSTGRESQL;

	static {
		POSTGRESQL = new PostgreSQLContainer<>("postgres:latest");
		POSTGRESQL.start();
	}
}
