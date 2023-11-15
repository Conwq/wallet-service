package org.example.walletservice.repository;

import org.example.walletservice.model.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing transaction-related data in the database.
 * This interface extends JpaRepository to provide CRUD operations for TransactionEntity.
 *
 * @see TransactionEntity
 */
@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
	/**
	 * Checks if a transaction entity with the given token exists in the database.
	 *
	 * @param token The token to check for existence.
	 * @return True if a transaction entity with the given token exists, false otherwise.
	 */
	boolean existsTransactionEntityByToken(String token);
}
