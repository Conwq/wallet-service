package ru.patseev.auditspringbootstarter.logger.bean;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import ru.patseev.auditspringbootstarter.logger.model.Log;

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
	public void recordAction(Log log) {
		final String requestToAddPlayerActions = """
				INSERT INTO wallet_service.log(log, player_id)
				VALUES (?, ?)
				""";

		jdbcTemplate.update(requestToAddPlayerActions,
				ps -> {
					ps.setString(1, log.getLog());
					ps.setInt(2, 1);
				});
	}
}
