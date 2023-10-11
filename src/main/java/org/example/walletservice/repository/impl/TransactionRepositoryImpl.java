package org.example.walletservice.repository.impl;

import org.example.walletservice.database.CustomDatabase;
import org.example.walletservice.model.Player;
import org.example.walletservice.repository.TransactionRepository;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

public final class TransactionRepositoryImpl implements TransactionRepository {
	private final CustomDatabase customDatabase;

	public TransactionRepositoryImpl(CustomDatabase customDatabase){
		this.customDatabase = customDatabase;
	}

	/**
	 * Checks for the presence of a unique transaction ID.
	 *
	 * @param transactionalToken A unique identifier for the transaction.
	 * @return {@code true} if the transaction ID exists, otherwise {@code false}.
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		return customDatabase.containToken(transactionalToken);
	}

	/**
	 * Carries out the operation of replenishment of the player's balance.
	 *
	 * @param inputPlayerAmount Deposit transaction amount.
	 * @param player The player from whom the deposit is being made.
	 * @param transactionalToken A unique identifier for the transaction.
	 */
	@Override
	public void credit(double inputPlayerAmount, Player player, String transactionalToken) {
		player.setBalance(player.getBalance() + inputPlayerAmount);

		saveTransactionToken(transactionalToken);

		recordTransactionInPlayerHistory(player,"credit",  transactionalToken, inputPlayerAmount);
	}

	/**
	 * Performs a debiting operation from the player's balance.
	 *
	 * @param inputPlayerAmount Amount of the debited operation.
	 * @param player The player from whom the charge is being made.
	 * @param transactionalToken A unique identifier for the transaction.
	 */
	@Override
	public void debit(double inputPlayerAmount, Player player, String transactionalToken) {
		player.setBalance(player.getBalance() - inputPlayerAmount);

		saveTransactionToken(transactionalToken);

		recordTransactionInPlayerHistory(player,"debit",  transactionalToken, inputPlayerAmount);
	}

	/**
	 * Retrieves the player's transaction history.
	 *
	 * @param username The player's username.
	 * @return A card representing the player's transactional history.
	 */
	@Override
	public Map<String, String> getPlayerTransactionalHistory(String username) {
		Optional<Player> player = customDatabase.getPlayer(username);
		return player.map(Player::getTransactionalHistory).orElse(null);
	}

	/**
	 * Gets the player's balance in the form of a line.
	 *
	 * @param player The player whose balance you want to get.
	 * @return String representation of the player's balance.
	 */
	@Override
	public String getPlayerBalance(Player player) {
		return String.valueOf(player.getBalance());
	}

	private void saveTransactionToken(String transactionToken) {
		customDatabase.saveTransactionToken(transactionToken);
	}

	/**
	 * Records the player's completed transaction in their history.
	 * @param player Player making the transaction
	 * @param transaction Transaction type
	 * @param transactionalToken Unique Transaction Token
	 * @param inputPlayerAmount Amount entered by the user
	 */
	private void recordTransactionInPlayerHistory(Player player, String transaction, String transactionalToken,
												  double inputPlayerAmount){

		String operation = transaction.equals("credit") ?  "Refill" : "Cash withdrawal";

		Map<String, String> playerTransactionalHistory = player.getTransactionalHistory();

		playerTransactionalHistory.put(transactionalToken,
				String.format("*****-%s-*****\n" +
								"\t-- Transaction number: %s\n" +
								"\t-- %s: %s\n" +
								"\t-- Your balance after transaction: %s\n" +
								"******************************************\n",
						Instant.now(), transactionalToken, operation, inputPlayerAmount, player.getBalance()));
	}
}