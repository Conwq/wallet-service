package org.example.walletservice.repository.manager;

import java.util.ResourceBundle;

public class DBResourceManager {

	private final ResourceBundle resourceBundle;

	public DBResourceManager() {
		this.resourceBundle = ResourceBundle.getBundle("database");
	}

	public String getValue(DBParameter parameter) {
		return resourceBundle.getString(parameter.toString());
	}
}