package org.example.walletservice.model.dto;

import org.example.walletservice.model.Role;

public record AuthPlayerDto(int playerID, String username, Role role) {
}
