package org.example.walletservice.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.math.BigDecimal;
import java.util.Collection;
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
public class PlayerEntity implements UserDetails {
	@Id
	@Column(name = "player_id")
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "wallet_service.player_id_seq")
	@SequenceGenerator(name = "wallet_service.player_id_seq", sequenceName = "wallet_service.player_id_seq", allocationSize = 1)
	private Integer playerID;
	@Column(name = "username")
	private String username;
	@Column(name = "password")
	private String password;
	@Column(name = "balance")
	private BigDecimal balance;
	@ManyToOne(fetch = FetchType.EAGER)
	@JoinColumn(name = "role_id", referencedColumnName = "role_id")
	private RoleEntity roleEntity;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "playerEntity")
	private List<LogEntity> logEntity;
	@OneToMany(fetch = FetchType.EAGER, mappedBy = "playerEntity")
	private List<TransactionEntity> listTransactionEntity;

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return List.of(new SimpleGrantedAuthority(roleEntity.getRole().name()));
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}

	@Override
	public String toString() {
		return String.format("Username - %s, Password - %s, Role - %s", username, password, roleEntity.getRole().name());
	}
}