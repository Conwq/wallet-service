package ru.patseev.auditspringbootstarter.logger.repository;


import ru.patseev.auditspringbootstarter.logger.model.Log;

/**
 * Logs all player actions
 */
public interface LoggerRepository {

	/**
	 * A method that writes logs to the log.
	 *
	 * @param log An object of type log that we will write to the database.
	 * @param playerID A player id
	 */
	void recordAction(Log log, int playerID);
}
