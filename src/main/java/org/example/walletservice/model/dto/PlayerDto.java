package org.example.walletservice.model.dto;

import org.example.walletservice.model.Role;

public record PlayerDto(int playerID, String username, Role role) {
}
