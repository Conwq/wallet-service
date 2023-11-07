package org.example.walletservice.repository.impl;

import org.testcontainers.containers.PostgreSQLContainer;

public abstract class AbstractPostgreSQLContainer {
	static PostgreSQLContainer<?> POSTGRES;

	static {
		POSTGRES = new PostgreSQLContainer<>("postgres:latest");
		POSTGRES.start();
	}
}
