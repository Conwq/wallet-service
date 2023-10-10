package org.example.walletservice.model;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * Represents a player in the wallet service with information such as username,
 * password, balance, and transactional history.
 */
public final class Player {
	private String username;
	private String password;
	private Role role;
	private double balance;
	private Map<String, String> transactionalHistory;

	public Player(String username, String password, Role role){
		this.username = username;
		this.password = password;
		this.role = role;
		this.balance = 0.0;
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

	public double getBalance() {
		return balance;
	}

	public void setBalance(double balance) {
		this.balance = balance;
	}

	public Map<String, String> getTransactionalHistory() {
		if (transactionalHistory == null){
			transactionalHistory = new HashMap<>();
		}
		return transactionalHistory;
	}

	public void setTransactionalHistory(Map<String, String> transactionalHistory) {
		this.transactionalHistory = transactionalHistory;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Player player = (Player) o;
		return Double.compare(player.balance, balance) == 0 && Objects.equals(username, player.username) && Objects.equals(password, player.password) && role == player.role && Objects.equals(transactionalHistory, player.transactionalHistory);
	}

	@Override
	public int hashCode() {
		return Objects.hash(username, password, role, balance, transactionalHistory);
	}
}