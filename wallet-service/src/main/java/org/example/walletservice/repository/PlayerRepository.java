package org.example.walletservice.repository;

import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PlayerRepository extends JpaRepository<PlayerEntity, Integer> {
	Optional<PlayerEntity> findByUsername(String username);
}
