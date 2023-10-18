package org.example.walletservice.repository.impl;

import org.example.walletservice.model.entity.Log;
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
	public void recordAction(Log log) {
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"INSERT INTO wallet_service.log(log, player_id) VALUES (?, ?)");

			statement.setString(1, log.getLog());
			statement.setInt(2, log.getPlayerID());
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			System.out.println("There is an error with the database. Try again later.");
		} finally {
			connectionProvider.closeConnection(connection, statement);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findAllActivityRecords() {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement("SELECT * FROM wallet_service.log");
			resultSet = statement.executeQuery();

			List<Log> playerLogRecords = new ArrayList<>();
			while (resultSet.next()) {
				playerLogRecords.add(mapToLog(resultSet));
			}
			return playerLogRecords;
		} catch (SQLException e) {
			System.out.println("There is an error with the database. Try again later.");
			return null;
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findActivityRecordsForPlayer(int playerID) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement("SELECT * FROM wallet_service.log WHERE player_id = ?");
			statement.setInt(1, playerID);
			resultSet = statement.executeQuery();

			List<Log> recordsPlayer = new ArrayList<>();
			while (resultSet.next()) {
				recordsPlayer.add(mapToLog(resultSet));
			}
			return recordsPlayer;
		} catch (SQLException e) {
			return null;
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}

	/**
	 * Converts the resulting ResultSet to a Log object
	 *
	 * @param resultSet ResultSet object from which we will get the values.
	 * @return Log object.
	 */
	private Log mapToLog(ResultSet resultSet) throws SQLException {
		return Log.builder().logID(resultSet.getInt("log_id"))
				.log(resultSet.getString("log"))
				.playerID(resultSet.getInt("player_id")).build();
	}
}
