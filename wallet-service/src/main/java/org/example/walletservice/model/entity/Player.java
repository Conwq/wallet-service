package org.example.walletservice.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.example.walletservice.model.enums.Role;

import java.math.BigDecimal;

/**
 * Represents a player in the wallet service with information such as username,
 * password, balance, and transactional history.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public final class Player {
	private Integer playerID;
	private String username;
	private String password;
	private Role role;
	private BigDecimal balance;
}