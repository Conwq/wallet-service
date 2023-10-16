package org.example.walletservice.repository.manager;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public record ConnectionProvider(String url, String username, String password) {

	public Connection takeConnection() throws SQLException {
		return DriverManager.getConnection(url, username, password);
	}
}
