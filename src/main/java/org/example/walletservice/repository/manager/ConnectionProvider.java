package org.example.walletservice.repository.manager;

import org.example.walletservice.repository.manager.DBParameter;
import org.example.walletservice.repository.manager.DBResourceManager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionProvider {
	private final String url;
	private final String username;
	private final String password;

	public ConnectionProvider(DBResourceManager resourceManager){
		this.url = resourceManager.getValue(DBParameter.URL);
		this.username = resourceManager.getValue(DBParameter.USER);
		this.password = resourceManager.getValue(DBParameter.PASSWORD);
		try {
			Class.forName(resourceManager.getValue(DBParameter.DRIVER));
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	public Connection takeConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
