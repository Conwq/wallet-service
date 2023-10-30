package org.example.walletservice.model.dto;

import java.math.BigDecimal;

/**
 * Data Transfer Object (DTO) for representing the balance response.
 *
 * @param username The username associated with the player.
 * @param balance  The balance amount associated with the player.
 */
public record BalanceResponseDto(String username, BigDecimal balance) {
}
