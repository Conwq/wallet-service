package org.example.walletservice.model.dto;

/**
 * Data transfer object representing a player request.
 *
 * @param username The player's username.
 * @param password The player's password.
 */
public record PlayerRequestDto(String username, String password) {
}
