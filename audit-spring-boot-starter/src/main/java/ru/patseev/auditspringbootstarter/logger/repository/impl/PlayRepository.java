package ru.patseev.auditspringbootstarter.logger.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

/**
 * Repository class for accessing player-related data in the database.
 * This class provides methods to interact with the database and retrieve player information.
 */
@Repository
public class PlayRepository {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public PlayRepository(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * Retrieves the player ID based on the provided username.
	 *
	 * @param username The username of the player.
	 * @return The player ID associated with the provided username.
	 */
	public int findIdByUsername(String username) {
		final String sql = "SELECT player_id FROM wallet_service.players WHERE username = ?";
		return jdbcTemplate.queryForObject(sql, Integer.class, username);
	}
}
