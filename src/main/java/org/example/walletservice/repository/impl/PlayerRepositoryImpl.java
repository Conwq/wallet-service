package org.example.walletservice.repository.impl;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.util.Objects;
import java.util.Optional;

/**
 * Manage player data and transactions.
 */
@Repository
public final class PlayerRepositoryImpl implements PlayerRepository {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PlayerRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Optional<Player> findPlayer(String username) {
		final String queryToGetUserByUsername = """
				SELECT * FROM wallet_service.players
				JOIN wallet_service.roles ON wallet_service.players.role_id = wallet_service.roles.role_id
				WHERE wallet_service.players.username = ?
				""";

		try {
			Player player = jdbcTemplate.queryForObject(queryToGetUserByUsername, (resultSet, rowNum) ->
					new Player(resultSet.getInt("player_id"),
							resultSet.getString("username"),
							resultSet.getString("password"),
							Role.valueOf(resultSet.getString("role_name").toUpperCase()),
							resultSet.getBigDecimal("balance")), username);
			return Optional.ofNullable(player);

		} catch (EmptyResultDataAccessException e) {
			return Optional.empty();
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int registrationPayer(Player player) {
		final String requestToAddPlayer = """
				INSERT INTO wallet_service.players(username, password, balance, role_id)
				VALUES(?, ?, 0, 1)
				""";

		KeyHolder keyHolder = new GeneratedKeyHolder();

		jdbcTemplate.update(connection -> {
			PreparedStatement ps = connection.prepareStatement(requestToAddPlayer, new String[]{"player_id"});
			ps.setString(1, player.getUsername());
			ps.setString(2, player.getPassword());
			return ps;
		}, keyHolder);

		return Objects.requireNonNull(keyHolder.getKey()).intValue();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Player findPlayerBalance(Player player) {
		final String requestForUserBalance = """
				SELECT * FROM wallet_service.players WHERE player_id = ?
				""";

		return jdbcTemplate.queryForObject(requestForUserBalance, (rs, rowNum) ->
						new Player(rs.getString("username"), rs.getBigDecimal("balance")),
				player.getPlayerID());
	}
}