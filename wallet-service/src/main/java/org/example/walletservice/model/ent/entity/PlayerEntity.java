package org.example.walletservice.model.ent.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

/**
 * Represents a player in the wallet service with information such as username,
 * password, balance, and transactional history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(schema = "wallet_service", name = "players")
public final class PlayerEntity {
	@Id
	@Column(name = "player_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "player_id_seq")
	@SequenceGenerator(name = "player_id_seq", sequenceName = "player_id_seq", allocationSize = 1)
	private Integer playerID;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "balance")
	private BigDecimal balance;
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "role_id", referencedColumnName = "role_id")
	private RoleEntity roleEntity;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "playerEntity")
	private List<LogEntity> logEntity;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "playerEntity")
	private List<TransactionEntity> listTransactionEntity;
}