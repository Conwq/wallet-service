package org.example.walletservice.model.entity;

import org.example.walletservice.model.Role;

import java.math.BigDecimal;
import java.util.Objects;

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

	public Player(String username, BigDecimal balance) {
		this.username = username;
		this.balance = balance;
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

	@Override
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null || getClass() != o.getClass()) return false;
		Player player = (Player) o;
		return Objects.equals(playerID, player.playerID) && Objects.equals(username, player.username)
				&& Objects.equals(password, player.password) && role == player.role && Objects.equals(balance, player.balance);
	}

	@Override
	public int hashCode() {
		return Objects.hash(playerID, username, password, role, balance);
	}

	@Override
	public String toString() {
		return "Player{" +
				"playerID=" + playerID +
				", username='" + username + '\'' +
				", password='" + password + '\'' +
				", role=" + role +
				", balance=" + balance +
				'}';
	}
}