package org.example.walletservice.repository;

import org.example.walletservice.model.Player;

import java.util.Map;

public interface TransactionRepository {
	boolean checkTokenExistence(String transactionalToken);
	void credit(double amountTransaction, Player player, String transactionToken);
	void debit(double inputPlayerAmount, Player player, String transactionalToken);
	Map<String, String> getPlayerTransactionalHistory(String username);
	String getPlayerBalance(Player player);
}
