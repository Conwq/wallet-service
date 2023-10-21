package org.example.walletservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

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
}
