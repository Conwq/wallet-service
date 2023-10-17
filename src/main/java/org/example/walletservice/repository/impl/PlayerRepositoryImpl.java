package org.example.walletservice.repository.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.sql.*;
import java.util.Optional;

/**
 * Implement the {@link PlayerRepository} interface, manage player data and transactions.
 */
public final class PlayerRepositoryImpl implements PlayerRepository {
	private final ConnectionProvider connectionProvider;

	public PlayerRepositoryImpl(ConnectionProvider connectionProvider) {
		this.connectionProvider = connectionProvider;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Player> findPlayer(String username) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"SELECT * FROM wallet_service.players " +
							"JOIN wallet_service.roles " +
							"ON wallet_service.players.role_id = wallet_service.roles.role_id " +
							"WHERE wallet_service.players.username = ?");

			statement.setString(1, username);
			resultSet = statement.executeQuery();
			if (resultSet.next()) {
				Player player = Player.builder().playerID(resultSet.getInt("player_id"))
						.username(resultSet.getString("username"))
						.password(resultSet.getString("password"))
						.role(Role.valueOf(resultSet.getString("role_name").toUpperCase())).build();
				return Optional.of(player);
			}
			return Optional.empty();
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
	public int registrationPayer(Player player) {
		Connection connection = null;
		PreparedStatement statementToSaveUser = null;
		PreparedStatement statementCreateNewBalance = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statementToSaveUser = connection.prepareStatement(
					"INSERT INTO wallet_service.players(username, password, balance, role_id) " +
							"VALUES(?, ?, 0.0, 1)", Statement.RETURN_GENERATED_KEYS);
			statementToSaveUser.setString(1, player.getUsername());
			statementToSaveUser.setString(2, player.getPassword());
			statementToSaveUser.executeUpdate();
			resultSet = statementToSaveUser.getGeneratedKeys();
			resultSet.next();
			int playerID = resultSet.getInt(1);
			connection.commit();
			return playerID;
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statementToSaveUser, resultSet);
			connectionProvider.closeConnection(statementCreateNewBalance);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public double findPlayerBalanceByPlayerID(int playerID) {
		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(
					"SELECT balance FROM wallet_service.players WHERE player_id = ?");

			statement.setInt(1, playerID);
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getDouble("balance");
			}
			return 0.0;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}
}