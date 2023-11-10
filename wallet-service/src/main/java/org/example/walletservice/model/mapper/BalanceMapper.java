package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper interface for converting balance-related data between entities and DTOs.
 */
@Mapper(componentModel = "spring")
public interface BalanceMapper {

	/**
	 * Converts the provided username and balance values to a {@link BalanceResponseDto}.
	 *
	 * @param playerEntity Player entity
	 */
	@Mapping(target = "username", source = "username")
	@Mapping(target = "balance", source = "balance")
	BalanceResponseDto toDto(PlayerEntity playerEntity);
}