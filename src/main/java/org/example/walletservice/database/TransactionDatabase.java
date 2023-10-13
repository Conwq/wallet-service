package org.example.walletservice.database;

import java.util.*;

public final class TransactionDatabase {
	private final Map<String, List<String>> entireHistoryUserTransactions = new LinkedHashMap<>();
	private final Set<String> transactionTokens = new HashSet<>();
	private final Map<String, Double> playerBalanceDetails = new HashMap<>() {{
		put("admin", 0.0);
	}};

	public void saveTransactionToken(String transactionToken) {
		transactionTokens.add(transactionToken);
	}

	public double findPlayerBalanceByUsername(String username) {
		return playerBalanceDetails.get(username);
	}

	public void savePlayersNewAmountFunds(String username, double newPlayerScore) {
		playerBalanceDetails.put(username, newPlayerScore);
	}

	public List<String> findPlayersTransactionHistoryByUsername(String username) {
		return entireHistoryUserTransactions.getOrDefault(username, new ArrayList<>());
	}

	public boolean containTransactionToken(String transactionToken) {
		return transactionTokens.contains(transactionToken);
	}

	public void savePlayersTransactionHistory(String username,
											  List<String> playersTransactionHistory) {
		entireHistoryUserTransactions.put(username, playersTransactionHistory);
	}
}