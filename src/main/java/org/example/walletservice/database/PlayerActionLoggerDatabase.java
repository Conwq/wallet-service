package org.example.walletservice.database;

import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class PlayerActionLoggerDatabase {
	private final Map<String, List<String>> activityRecords = new TreeMap<>();

	public List<String> findActivityRecordsForPlayer(String username) {
		return activityRecords.get(username);
	}

	public void recordAction(String username, List<String> playersActionLogg) {
		activityRecords.put(username, playersActionLogg);
	}

	public Map<String, List<String>> findAllActivityRecords() {
		return activityRecords;
	}
}
