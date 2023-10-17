package org.example.walletservice.repository.impl;

import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;
import org.example.walletservice.service.enums.Operation;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public final class TransactionRepositoryImpl implements TransactionRepository {
	private final ConnectionProvider connectionProvider;

	public TransactionRepositoryImpl(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void creditOrDebit(double newPlayerAmount, int playerID, String transactionalToken, Operation operation) {
		String operationValue = operation == Operation.CREDIT ? operation.toString() : Operation.DEBIT.toString();
		Connection connection = null;
		PreparedStatement statementToChangePlayerBalance = null;

		try {
			connection = connectionProvider.takeConnection();
			statementToChangePlayerBalance = connection.prepareStatement(
					"UPDATE wallet_service.players SET balance = ? WHERE player_id = ?");

			statementToChangePlayerBalance.setDouble(1, newPlayerAmount);
			statementToChangePlayerBalance.setInt(2, playerID);
			statementToChangePlayerBalance.executeUpdate();
			recordTransactionInPlayerHistory(connection, playerID, operationValue, transactionalToken,
					newPlayerAmount);

			connection.commit();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statementToChangePlayerBalance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"SELECT token FROM wallet_service.transaction WHERE token = ?");

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
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"SELECT record FROM wallet_service.transaction WHERE player_id = ?");

			statement.setInt(1, playerID);
			resultSet = statement.executeQuery();
			List<String> transactionHistory = new ArrayList<>();
			while (resultSet.next()) {
				if (resultSet.getString("record") != null) {
					transactionHistory.add(resultSet.getString("record"));
				}
			}
			return transactionHistory;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}

	/**
	 * Records the player's completed transaction in their history.
	 *
	 * @param playerID           The ID of the player making the transaction
	 * @param operation          Operation type
	 * @param transactionalToken Unique Transaction Token
	 * @param newPlayerAmount    New player balance.
	 */
	private void recordTransactionInPlayerHistory(Connection connection, int playerID, String operation,
												  String transactionalToken, double newPlayerAmount) {
		PreparedStatement statement = null;

		try {
			statement = connection.prepareStatement(
					"INSERT INTO wallet_service.transaction (record, player_id, token) VALUES (?, ?, ?)");

			statement.setString(1, String.format("*****************-%s-*****************\n" +
							"\t-- Transaction number: %s\n" +
							"\t-- Your balance after transaction: %s\n" +
							"******************************************\n",
					operation, transactionalToken, newPlayerAmount));
			statement.setInt(2, playerID);
			statement.setString(3, transactionalToken);
			statement.executeUpdate();
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(statement);
		}
	}
}