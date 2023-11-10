package org.example.walletservice.repository.rep.impl;

import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRep extends JpaRepository<PlayerEntity, Integer> {
	Optional<PlayerEntity> findByUsername(String username);
}
