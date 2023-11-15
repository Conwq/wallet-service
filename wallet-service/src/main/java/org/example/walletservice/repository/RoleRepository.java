package org.example.walletservice.repository;

import org.example.walletservice.model.entity.RoleEntity;
import org.example.walletservice.model.enums.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for accessing role-related data in the database.
 * This interface extends JpaRepository to provide CRUD operations for RoleEntity.
 *
 * @see RoleEntity
 */
@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, Integer> {
	/**
	 * Retrieves a RoleEntity based on the provided role name.
	 *
	 * @param role The role name to search for.
	 * @return The RoleEntity associated with the provided role name, or null if not found.
	 */
	@Query("FROM RoleEntity WHERE role = :role")
	RoleEntity findByRoleName(@Param("role") Role role);
}
