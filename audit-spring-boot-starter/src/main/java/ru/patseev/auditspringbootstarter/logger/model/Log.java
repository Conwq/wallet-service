package ru.patseev.auditspringbootstarter.logger.model;

import java.util.Objects;

/**
 * A data class representing a log entry.
 * This class is annotated with Lombok annotations for generating boilerplate code.
 */
public class Log {
	private int logID;
	private String log;
	private int playerID;

	public Log() {
	}

	public int getLogID() {
		return logID;
	}

	public void setLogID(int logID) {
		this.logID = logID;
	}

	public String getLog() {
		return log;
	}

	public void setLog(String log) {
		this.log = log;
	}

	public int getPlayerID() {
		return playerID;
	}

	public void setPlayerID(int playerID) {
		this.playerID = playerID;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Log log1 = (Log) o;
		return logID == log1.logID && playerID == log1.playerID && Objects.equals(log, log1.log);
	}

	@Override
	public int hashCode() {
		return Objects.hash(logID, log, playerID);
	}

	@Override
	public String toString() {
		return "Log{" +
				"logID=" + logID +
				", log='" + log + '\'' +
				", playerID=" + playerID +
				'}';
	}
}
