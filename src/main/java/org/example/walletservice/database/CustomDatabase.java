package org.example.walletservice.database;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;

import java.util.*;

public final class CustomDatabase {
	private static CustomDatabase instance;
	private static final Set<String> transactionNumbers = new HashSet<>();
	private final Map<String, Player> playersData = new LinkedHashMap<>(){{
		put("admin", new Player("admin", "admin", Role.ADMIN));
	}};

	private CustomDatabase(){}

	public static CustomDatabase getInstance() {
		if (instance == null){
			instance = new CustomDatabase();
		}
		return instance;
	}

	public void savePlayer(String username, Player player){
		playersData.put(username, player);
	}

	public Optional<Player> getPlayer(String username) {
		return Optional.ofNullable(playersData.get(username));
	}

	public void saveTransactionToken(String transactionToken){
		transactionNumbers.add(transactionToken);
	}

	public boolean containToken(String transactionToken){
		return transactionNumbers.contains(transactionToken);
	}
}