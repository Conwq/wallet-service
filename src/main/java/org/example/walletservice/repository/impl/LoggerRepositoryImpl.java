package org.example.walletservice.repository.impl;

import org.example.walletservice.model.entity.Log;
import org.example.walletservice.repository.LoggerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

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
					ps.setInt(2, log.getPlayerID());
				});
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findAllActivityRecords() {
		final String requestToReceiveAllActions = """
				SELECT * FROM wallet_service.log
				""";

		return jdbcTemplate.query(requestToReceiveAllActions,
				(rs, rowNum) -> mapToLog(rs));
	}


	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<Log> findActivityRecordsForPlayer(int playerID) {
		final String requestToReceiveAllPlayerActions = """
				SELECT * FROM wallet_service.log WHERE player_id = ?
				""";

		return jdbcTemplate.query(requestToReceiveAllPlayerActions,
				(rs, rowNum) -> mapToLog(rs),
				playerID);
	}

	/**
	 * Converts the resulting ResultSet to a Log object
	 *
	 * @param resultSet ResultSet object from which we will get the values.
	 * @return Log object.
	 */
	private Log mapToLog(ResultSet resultSet) throws SQLException {
		Log log = new Log();
		log.setLogID(resultSet.getInt("log_id"));
		log.setLog(resultSet.getString("log"));
		log.setPlayerID(resultSet.getInt("player_id"));
		return log;
	}
}
