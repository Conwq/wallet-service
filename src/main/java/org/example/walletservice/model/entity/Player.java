package org.example.walletservice.model.entity;

import org.example.walletservice.model.Role;

import java.math.BigDecimal;

/**
 * Represents a player in the wallet service with information such as username,
 * password, balance, and transactional history.
 */
public final class Player {
	private Integer playerID;
	private String username;
	private String password;
	private Role role;
	private BigDecimal balance;

	public Player() {
	}

	public Player(Integer playerID, String username, String password, Role role, BigDecimal balance) {
		this.playerID = playerID;
		this.username = username;
		this.password = password;
		this.role = role;
		this.balance = balance;
	}

	public Integer getPlayerID() {
		return playerID;
	}

	public void setPlayerID(Integer playerID) {
		this.playerID = playerID;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public Role getRole() {
		return role;
	}

	public void setRole(Role role) {
		this.role = role;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
}