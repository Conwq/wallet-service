package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.BalanceResponseDto;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

@Mapper(componentModel = "spring")
public interface BalanceMapper {
	default BalanceResponseDto toDto(String username, BigDecimal balance) {
		return new BalanceResponseDto(username, balance);
	}
}
