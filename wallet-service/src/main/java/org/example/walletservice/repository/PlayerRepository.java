package org.example.walletservice.repository;

import org.example.walletservice.model.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository interface for accessing player-related data in the database.
 * This interface extends JpaRepository to provide CRUD operations for PlayerEntity.
 *
 * @see PlayerEntity
 */
@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Integer> {
	/**
	 * Retrieves a PlayerEntity based on the provided username.
	 *
	 * @param username The username to search for.
	 * @return An Optional containing the PlayerEntity associated with the provided username, or an empty Optional if not found.
	 */
	Optional<PlayerEntity> findByUsername(String username);
}
