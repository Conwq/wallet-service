package org.example.walletservice.repository;

import java.util.List;

public interface TransactionRepository {
	boolean checkTokenExistence(String transactionalToken);

	void credit(double amountTransaction, String username, String transactionToken);

	void debit(double inputPlayerAmount, String username, String transactionalToken);

	List<String> findPlayerTransactionalHistoryByUsername(String username);

	double findPlayerBalanceByUsername(String username);
}
