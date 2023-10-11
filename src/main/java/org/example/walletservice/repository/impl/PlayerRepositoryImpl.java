package org.example.walletservice.repository.impl;

import org.example.walletservice.model.Player;
import org.example.walletservice.model.Role;
import org.example.walletservice.repository.PlayerRepository;

import java.time.Instant;
import java.util.*;

/**
 * Реализация интерфейса {@link PlayerRepository}, управление данными игроков и транзакциями.
 */
public final class PlayerRepositoryImpl implements PlayerRepository {
	private static final Map<String, Player> playersData = new HashMap<>(){{
		put("admin", new Player("admin", "admin", Role.ADMIN));
	}};

	private static final Set<String> transactionNumbers = new HashSet<>();

	/**
	 * Finds a player by their username.
	 *
	 * @param username The player's username.
	 * @return Optional object containing the player if found or empty if not found.
	 */
	@Override
	public Optional<Player> findPlayer(String username){
		return Optional.ofNullable(playersData.get(username));
	}

	/**
	 * Registers a new player.
	 *
	 * @param player to register.
	 */
	@Override
	public void registrationPayer(Player player) {
		playersData.put(player.getUsername(), player);
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

		recordInUserTransactionHistory(player,"credit",  transactionalToken, inputPlayerAmount);

		transactionNumbers.add(transactionalToken);
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

		recordInUserTransactionHistory(player,"debit",  transactionalToken, inputPlayerAmount);

		transactionNumbers.add(transactionalToken);
	}

	/**
	 * Retrieves the player's transaction history.
	 *
	 * @param username The player's username.
	 * @return A card representing the player's transactional history.
	 */
	@Override
	public Map<String, String> getPlayerTransactionalHistory(String username) {
		Player player = findPlayer(username).get();
		return player.getTransactionalHistory();
	}

	/**
	 * Checks for the presence of a unique transaction ID.
	 *
	 * @param transactionalToken A unique identifier for the transaction.
	 * @return {@code true} if the transaction ID exists, otherwise {@code false}.
	 */
	@Override
	public boolean checkTokenExistence(String transactionalToken) {
		return transactionNumbers.contains(transactionalToken);
	}

	/**
	 * Records the player's completed transaction in their history.
	 * @param player Player making the transaction
	 * @param transaction Transaction type
	 * @param transactionalToken Unique Transaction Token
	 * @param inputPlayerAmount Amount entered by the user
	 */
	private void recordInUserTransactionHistory(Player player, String transaction, String transactionalToken,
											   double inputPlayerAmount){

		String operation = transaction.equals("credit") ?  "Refill" : "Cash withdrawal";

		Map<String, String> playerTransactionalHistory = player.getTransactionalHistory();

		playerTransactionalHistory.put(transactionalToken,
				String.format("*****-%s-*****\n" +
						"\t-- Transaction number: %s\n" +
						"\t-- %s: %s\n" +
						"\t-- Your balance after transaction: %s\n" +
						"****************************************\n",
				Instant.now(), transactionalToken, operation, inputPlayerAmount, player.getBalance()));
	}
}