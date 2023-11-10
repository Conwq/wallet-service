package org.example.walletservice.repository;

import org.example.walletservice.model.ent.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository extends JpaRepository<TransactionEntity, Integer> {
	boolean existsTransactionEntityByToken(String token);
}
