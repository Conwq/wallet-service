package org.example.walletservice.repository;

import org.example.walletservice.model.Player;

import java.util.Optional;

/**
 * This interface defines what behavior the classes that implement it will have.
 */
public interface PlayerRepository {
	Optional<Player> findPlayer(String username);
	void registrationPayer(Player player);
}