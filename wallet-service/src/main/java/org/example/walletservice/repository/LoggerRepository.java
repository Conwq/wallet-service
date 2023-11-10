package org.example.walletservice.repository;

import org.example.walletservice.model.ent.entity.LogEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LoggerRepository extends JpaRepository<LogEntity, Integer> {
}
