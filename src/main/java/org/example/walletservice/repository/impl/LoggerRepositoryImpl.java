package org.example.walletservice.repository.impl;

import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class LoggerRepositoryImpl implements LoggerRepository {
	private final ConnectionProvider connectionProvider;

	public LoggerRepositoryImpl(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordAction(int playerID, String playerAction) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"INSERT INTO wallet_service.log(player_id, log) VALUES (?, ?)");

			statement.setInt(1, playerID);
			statement.setString(2, playerAction);
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statement);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findAllActivityRecords() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement("SELECT log FROM wallet_service.log");
			resultSet = statement.executeQuery();

			List<String> playerLogRecords = new ArrayList<>();
			while (resultSet.next()) {
				String logEntry = resultSet.getString("log");
				playerLogRecords.add(logEntry);
			}
			return playerLogRecords;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> findActivityRecordsForPlayer(int playerID) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement("SELECT log FROM wallet_service.log WHERE player_id = ?");

			statement.setInt(1, playerID);
			resultSet = statement.executeQuery();
			List<String> recordsPlayer = new ArrayList<>();
			while (resultSet.next()) {
				String recordPlayer = resultSet.getString("log");
				recordsPlayer.add(recordPlayer);
			}
			return recordsPlayer;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}
}
