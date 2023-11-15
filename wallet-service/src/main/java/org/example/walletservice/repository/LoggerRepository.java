package org.example.walletservice.repository;

import org.example.walletservice.model.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing log-related data in the database.
 * This interface extends JpaRepository to provide CRUD operations for LogEntity.
 *
 * @see LogEntity
 */
@Repository
public interface LoggerRepository extends JpaRepository<LogEntity, Integer> {
}
