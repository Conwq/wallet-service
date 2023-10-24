package org.example.walletservice.repository.manager;

import java.util.ResourceBundle;

public class DBResourceManager {
	private final ResourceBundle resourceBundle;
	private final String propertiesFile;

	public DBResourceManager(String propertiesFileName) {
		this.propertiesFile = propertiesFileName;
		this.resourceBundle = ResourceBundle.getBundle(propertiesFile);
	}

	public String getValue(String key) {
		return resourceBundle.getString(key);
	}
}