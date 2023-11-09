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
	 */
	void recordAction(Log log);
}
