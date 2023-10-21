package org.example.walletservice.repository.impl;

import org.example.walletservice.model.entity.Log;
import org.example.walletservice.repository.LoggerRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class LoggerRepositoryImpl implements LoggerRepository {
	private final ConnectionProvider connectionProvider;
	private static final String LOG = "log";
	private static final String LOG_ID = "log_id";
	private static final String PLAYER_ID = "player_id";

	public LoggerRepositoryImpl(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordAction(Log log) {
		final String REQUEST_TO_ADD_PLAYER_ACTIONS = """
				INSERT INTO wallet_service.log(log, player_id) 
				VALUES (?, ?)
				""";

		Connection connection = null;
		PreparedStatement statement = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(REQUEST_TO_ADD_PLAYER_ACTIONS);

			statement.setString(1, log.getLog());
			statement.setInt(2, log.getPlayerID());
			statement.executeUpdate();
			connection.commit();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			System.out.println("[FAIL] Database error.");
		} finally {
			connectionProvider.closeConnection(connection, statement);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findAllActivityRecords() {
		final String REQUEST_TO_RECEIVE_ALL_ACTIONS = """
				SELECT * FROM wallet_service.log
				""";

		try (Connection connection = connectionProvider.takeConnection();
			 Statement statement = connection.createStatement();
			 ResultSet resultSet = statement.executeQuery(REQUEST_TO_RECEIVE_ALL_ACTIONS)) {

			List<Log> playerLogRecords = new ArrayList<>();
			while (resultSet.next()) {
				playerLogRecords.add(mapToLog(resultSet));
			}
			return playerLogRecords;
		} catch (SQLException e) {
			return null;
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findActivityRecordsForPlayer(int playerID) {
		final String REQUEST_TO_RECEIVE_ALL_PLAYER_ACTIONS = """
				SELECT * FROM wallet_service.log WHERE player_id = ?
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(REQUEST_TO_RECEIVE_ALL_PLAYER_ACTIONS);
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
		Log log = new Log();
		log.setLogID(resultSet.getInt(LOG_ID));
		log.setLog(resultSet.getString(LOG));
		log.setPlayerID(resultSet.getInt(PLAYER_ID));
		return log;
	}
}
