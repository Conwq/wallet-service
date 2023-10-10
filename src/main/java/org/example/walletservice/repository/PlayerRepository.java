package org.example.walletservice.repository;

import org.example.walletservice.model.Player;

import java.util.Map;
import java.util.Optional;

/**
 * This interface defines what behavior the classes that implement it will have.
 */
public interface PlayerRepository {
	Optional<Player> findPlayer(String username);
	void registrationPayer(Player player);
	void credit(double amountTransaction, Player player, String transactionalToken);
	void debit(double inputPlayerAmount, Player player, String transactionalToken);
	Map<String, String> getPlayerTransactionalHistory(String username);
	boolean checkTokenExistence(String transactionalToken);
	String getPlayerBalance(Player player);
}