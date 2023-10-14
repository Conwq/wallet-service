package org.example.walletservice.repository.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
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
		ResultSet resultSet = null;
		try (Connection connection = connectionProvider.takeConnection();
			 PreparedStatement statement = connection.prepareStatement("SELECT * FROM wallet_service.players " +
					 "JOIN wallet_service.roles " +
					 "ON wallet_service.players.role_id = wallet_service.roles.role_id " +
					 "WHERE wallet_service.players.username = ?")) {

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
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int registrationPayer(Player player) {
		ResultSet resultSet = null;
		try (Connection connection = connectionProvider.takeConnection();
			 PreparedStatement statementToSaveUser = connection.prepareStatement(
					 "INSERT INTO wallet_service.players(username, password, role_id) VALUES(?, ?, 1)",
					 Statement.RETURN_GENERATED_KEYS);
			 PreparedStatement statementCreateNewBalance = connection.prepareStatement(
					 "INSERT INTO wallet_service.transaction(player_id) VALUES (?)")) {

			statementToSaveUser.setString(1, player.getUsername());
			statementToSaveUser.setString(2, player.getPassword());
			statementToSaveUser.executeUpdate();
			resultSet = statementToSaveUser.getGeneratedKeys();
			resultSet.next();

			int playerID = resultSet.getInt(1);

			statementCreateNewBalance.setInt(1, playerID);
			statementCreateNewBalance.executeUpdate();

			return playerID;
		} catch (SQLException e) {
			throw new RuntimeException(e);
		} finally {
			if (resultSet != null) {
				try {
					resultSet.close();
				} catch (SQLException e) {
					throw new RuntimeException(e);
				}
			}
		}
	}
}