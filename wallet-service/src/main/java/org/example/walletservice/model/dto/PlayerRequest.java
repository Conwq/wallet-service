package org.example.walletservice.model.dto;

import com.fasterxml.jackson.annotation.JsonAutoDetect;

/**
 * Data transfer object representing a player request.
 *
 * @param username The player's username.
 * @param password The player's password.
 */
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
public record PlayerRequest(String username, String password) {
}
