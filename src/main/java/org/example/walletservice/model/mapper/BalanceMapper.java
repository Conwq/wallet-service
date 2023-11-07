package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.BalanceResponseDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

/**
 * Mapper interface for converting balance-related data between entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface BalanceMapper {

	/**
	 * Converts the provided username and balance values to a {@link BalanceResponseDto}.
	 *
	 * @param username The username associated with the player.
	 * @param balance  The balance amount associated with the player.
	 * @return A {@link BalanceResponseDto} representing the balance response.
	 */
	default BalanceResponseDto toDto(String username, BigDecimal balance) {
		return new BalanceResponseDto(username, balance);
	}
}