package org.example.walletservice.model.ent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.walletservice.model.enums.Role;

import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "wallet_service", name = "roles")
public class RoleEntity {
	@Id
	@Column(name = "role_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "role_id_seq")
	@SequenceGenerator(name = "role_id_seq", sequenceName = "role_id_seq", allocationSize = 1)
	private int roleID;
	@Column(name = "role_name")
	@Enumerated(EnumType.STRING)
	private Role role;
	@OneToMany(mappedBy = "roleEntity", fetch = FetchType.LAZY)
	private List<PlayerEntity> playerEntity;

	@Override
	public String toString() {
		return role.name();
	}
}
