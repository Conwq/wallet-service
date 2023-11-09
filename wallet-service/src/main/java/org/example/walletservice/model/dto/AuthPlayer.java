package org.example.walletservice.model.dto;


import ru.patseev.auditspringbootstarter.logger.model.Roles;

/**
 * Data transfer object representing authenticated player information.
 *
 * @param playerID The player ID.
 * @param username The player's username.
 * @param role     The player's role.
 */
public record AuthPlayer(int playerID, String username, Roles role) {
}
