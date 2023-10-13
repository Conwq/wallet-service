package org.example.walletservice.database;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class PlayerDatabase {
	private final Map<String, Player> playersData = new LinkedHashMap<>() {{
		put("admin", new Player("admin", "admin", Role.ADMIN));
	}};

	public void savePlayer(String username, Player player) {
		playersData.put(username, player);
	}

	public Optional<Player> findPlayerByUsername(String username) {
		return Optional.ofNullable(playersData.get(username));
	}
}
