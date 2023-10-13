package org.example.walletservice.repository.impl;

import org.example.walletservice.database.PlayerActionLoggerDatabase;
import org.example.walletservice.repository.PlayerActionLoggerRepository;

import java.util.List;
import java.util.Map;

public class PlayerActionLoggerRepositoryImpl implements PlayerActionLoggerRepository {
	private final PlayerActionLoggerDatabase playerActionLoggerDatabase;

	public PlayerActionLoggerRepositoryImpl(PlayerActionLoggerDatabase playerActionLoggerDatabase) {
		this.playerActionLoggerDatabase = playerActionLoggerDatabase;
	}

	@Override
	public void recordAction(String username, List<String> playersActionLogg) {
		playerActionLoggerDatabase.recordAction(username, playersActionLogg);
	}

	@Override
	public Map<String, List<String>> findAllActivityRecords() {
		return playerActionLoggerDatabase.findAllActivityRecords();
	}

	@Override
	public List<String> findActivityRecordsForPlayer(String username) {
		return playerActionLoggerDatabase.findActivityRecordsForPlayer(username);
	}
}
