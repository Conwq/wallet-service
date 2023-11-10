package org.example.walletservice.repository;

import org.example.walletservice.model.ent.entity.RoleEntity;
import org.example.walletservice.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
	@Query("FROM RoleEntity WHERE role = :role")
	RoleEntity findByRoleName(@Param("role") Role role);
}
