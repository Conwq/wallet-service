package org.example.walletservice.repository.manager;

import java.sql.*;

/**
 * A simple utility class for managing database connections.
 * This class provides methods to obtain connections, close connections, and manage transactions.
 *
 * @param url      The JDBC URL of the database.
 * @param username The username for authenticating to the database.
 * @param password The password for authenticating to the database.
 */
public record ConnectionProvider(String url, String username, String password) {

	/**
	 * Retrieves a connection to the database. The connection has auto-commit turned off.
	 *
	 * @return A Connection object representing a connection to the database.
	 * @throws SQLException If a database access error occurs.
	 */
	public Connection takeConnection() throws SQLException {
		Connection connection = DriverManager.getConnection(url, username, password);
		connection.setAutoCommit(false);
		return connection;
	}

	/**
	 * Closes the provided ResultSet.
	 *
	 * @param resultSet The ResultSet to close.
	 */
	public void closeConnection(ResultSet resultSet) {
		if (resultSet != null) {
			try {
				resultSet.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Closes the provided Statement and ResultSet.
	 *
	 * @param statement  The Statement to close.
	 * @param connection The Connection to close.
	 */
	public void closeConnection(Connection connection, Statement statement) {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	/**
	 * Closes the provided Statement.
	 *
	 * @param statement The Statement to close.
	 */
	public void closeConnection(Statement statement) {
		if (statement != null) {
			try {
				statement.close();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}

	}

	/**
	 * Closes the provided Connection, Statement, and ResultSet.
	 *
	 * @param connection The Connection to close.
	 * @param statement  The Statement to close.
	 * @param resultSet  The ResultSet to close.
	 */
	public void closeConnection(Connection connection, Statement statement, ResultSet resultSet) {
		closeConnection(connection, statement);
		closeConnection(resultSet);
	}

	public void rollbackCommit(Connection connection) {
		try {
			connection.rollback();
		} catch (SQLException ex) {
			throw new RuntimeException(ex);
		}
	}
}