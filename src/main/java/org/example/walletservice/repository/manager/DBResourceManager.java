package org.example.walletservice.repository.manager;

import java.util.ResourceBundle;

public class DBResourceManager {
	private final ResourceBundle resourceBundle;
	private static final String PROPERTIES_FILE = "liquibase";

	public DBResourceManager() {
		this.resourceBundle = ResourceBundle.getBundle(PROPERTIES_FILE);
	}

	public String getValue(String key) {
		return resourceBundle.getString(key);
	}
}