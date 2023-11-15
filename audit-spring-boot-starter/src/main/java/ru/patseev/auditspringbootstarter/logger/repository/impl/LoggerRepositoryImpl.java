package ru.patseev.auditspringbootstarter.logger.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.patseev.auditspringbootstarter.logger.model.Log;
import ru.patseev.auditspringbootstarter.logger.repository.LoggerRepository;

@Repository
public class LoggerRepositoryImpl implements LoggerRepository {
	private final JdbcTemplate jdbcTemplate;

	@Autowired
	public LoggerRepositoryImpl(JdbcTemplate jdbcTemplate) {
		this.jdbcTemplate = jdbcTemplate;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void recordAction(Log log, int playerID) {
		System.out.println("Repository");
		final String requestToAddPlayerActions = """
				INSERT INTO wallet_service.log(log, player_id)
				VALUES (?, ?)
				""";

		jdbcTemplate.update(requestToAddPlayerActions, log.getLog(), playerID);
	}
}
