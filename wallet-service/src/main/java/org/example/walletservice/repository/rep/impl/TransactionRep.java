package org.example.walletservice.repository.rep.impl;

import org.example.walletservice.model.ent.entity.TransactionEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRep extends JpaRepository<TransactionEntity, Integer> {
}
