package org.example.walletservice.repository.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.manager.ConnectionProvider;

import java.math.BigDecimal;
import java.sql.*;
import java.util.Optional;

/**
 * Manage player data and transactions.
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
		final String QUERY_TO_GET_USER_BY_USERNAME = """
				SELECT * FROM wallet_service.players
				JOIN wallet_service.roles ON wallet_service.players.role_id = wallet_service.roles.role_id
				WHERE wallet_service.players.username = ?
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;
		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(QUERY_TO_GET_USER_BY_USERNAME);

			statement.setString(1, username);
			resultSet = statement.executeQuery();
			Player player = null;
			if (resultSet.next()) {
				player = new Player();
				player.setPlayerID(resultSet.getInt("player_id"));
				player.setUsername(resultSet.getString("username"));
				player.setPassword(resultSet.getString("password"));
				player.setRole(Role.valueOf(resultSet.getString("role_name").toUpperCase()));
			}
			return Optional.ofNullable(player);
		} catch (SQLException e) {
			System.out.println("[FAIL] Database error.");
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
		final String REQUEST_TO_ADD_PLAYER = """
				INSERT INTO wallet_service.players(username, password, balance, role_id) 
				VALUES(?, ?, 0.0, 1)
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(REQUEST_TO_ADD_PLAYER, Statement.RETURN_GENERATED_KEYS);
			statement.setString(1, player.getUsername());
			statement.setString(2, player.getPassword());
			statement.executeUpdate();
			resultSet = statement.getGeneratedKeys();
			resultSet.next();
			int playerID = resultSet.getInt(1);
			connection.commit();
			return playerID;
		} catch (SQLException e) {
			connectionProvider.rollbackCommit(connection);
			return -1;
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public BigDecimal findPlayerBalanceByPlayer(Player player) {
		final String REQUEST_FOR_USER_BALANCE = """
				SELECT balance FROM wallet_service.players WHERE player_id = ?
				""";

		Connection connection = null;
		PreparedStatement statement = null;
		ResultSet resultSet = null;

		try {
			connection = connectionProvider.takeConnection();
			statement = connection.prepareStatement(REQUEST_FOR_USER_BALANCE);

			statement.setInt(1, player.getPlayerID());
			resultSet = statement.executeQuery();

			if (resultSet.next()) {
				return resultSet.getBigDecimal("balance");
			}
			return BigDecimal.ZERO;
		} catch (SQLException e) {
			return BigDecimal.valueOf(-1);
		} finally {
			connectionProvider.closeConnection(connection, statement, resultSet);
		}
	}
}