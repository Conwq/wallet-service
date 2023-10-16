package org.example.walletservice.repository.manager;

import java.util.ResourceBundle;

public class DBResourceManager {

	private final ResourceBundle resourceBundle;

	public DBResourceManager() {
		this.resourceBundle = ResourceBundle.getBundle("liquibase");
	}

	public String getValue(String key) {
		return resourceBundle.getString(key);
	}
}