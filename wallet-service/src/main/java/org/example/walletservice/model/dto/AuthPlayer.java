package org.example.walletservice.model.dto;


import org.example.walletservice.model.enums.Role;

/**
 * Data transfer object representing authenticated player information.
 *
 * @param playerID The player ID.
 * @param username The player's username.
 * @param role     The player's role.
 */
public record AuthPlayer(int playerID, String username, Role role) {
}
