package org.example.walletservice.repository.impl;

import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class TransactionRepositoryImpl implements TransactionRepository {
	private final ConnectionProvider connectionProvider;
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String RECORD = "record";

	public TransactionRepositoryImpl(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void creditOrDebit(Transaction transaction, BigDecimal newPlayerAmount) {
		final String REQUEST_TO_UPDATE_PLAYER_BALANCE = """
				UPDATE wallet_service.players SET balance = ? WHERE player_id = ?
				""";

		Connection connection = null;
		PreparedStatement statementToChangePlayerBalance = null;

		try {
			connection = connectionProvider.takeConnection();
			statementToChangePlayerBalance = connection.prepareStatement(REQUEST_TO_UPDATE_PLAYER_BALANCE);
			statementToChangePlayerBalance.setBigDecimal(1, newPlayerAmount);
			statementToChangePlayerBalance.setInt(2, transaction.getPlayerID());
			statementToChangePlayerBalance.executeUpdate();
			recordTransactionInPlayerHistory(connection, transaction);
			connection.commit();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			System.out.println(ERROR_CONNECTION_DATABASE);
		} finally {
			connectionProvider.closeConnection(connection, statementToChangePlayerBalance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		final String TOKEN_REQUEST = """
				SELECT token FROM wallet_service.transaction WHERE token = ?
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(TOKEN_REQUEST);

			statement.setString(1, transactionalToken);
			resultSet = statement.executeQuery();
			return resultSet.next();
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
	public List<String> findPlayerTransactionalHistoryByPlayerID(int playerID) {
		final String RECORD_REQUEST = """
				SELECT record FROM wallet_service.transaction WHERE player_id = ?
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(RECORD_REQUEST);

			statement.setInt(1, playerID);
			resultSet = statement.executeQuery();
			List<String> transactionHistory = new ArrayList<>();
			while (resultSet.next()) {
				if (resultSet.getString(RECORD) != null) {
					transactionHistory.add(resultSet.getString(RECORD));
				}
			}
			return transactionHistory;
		} catch (SQLException e) {
			return null;
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}

	/**
	 * Records a transaction in the player's transaction history in the database.
	 *
	 * @param connection  The database connection.
	 * @param transaction The Transaction object containing details of the transaction.
	 */
	private void recordTransactionInPlayerHistory(Connection connection, Transaction transaction) {
		final String REQUEST_TO_ADD_TRANSACTION = """
				INSERT INTO wallet_service.transaction (record, token, operation, amount, player_id) 
				VALUES (?, ?, ?, ?, ?)
				""";

		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(REQUEST_TO_ADD_TRANSACTION);
			statement.setString(1, transaction.getRecord());
			statement.setString(2, transaction.getToken());
			statement.setString(3, transaction.getOperation());
			statement.setBigDecimal(4, transaction.getAmount());
			statement.setInt(5, transaction.getPlayerID());
			statement.executeUpdate();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			System.out.println(ERROR_CONNECTION_DATABASE);
		} finally {
			connectionProvider.closeConnection(statement);
		}
	}
}