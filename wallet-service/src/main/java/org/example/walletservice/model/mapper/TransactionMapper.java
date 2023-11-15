package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.entity.PlayerEntity;
import org.example.walletservice.model.entity.TransactionEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for converting between Transaction entity and TransactionRequestDto.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

	/**
	 * Converts TransactionRequestDto, Player entity, Operation, and new player balance to a Transaction entity.
	 *
	 * @param transactionRequestDto              The TransactionRequestDto.
	 * @param playerEntity           The Player entity.
	 * @return The Transaction entity.
	 */
	default TransactionEntity toEntity(TransactionRequestDto transactionRequestDto, PlayerEntity playerEntity) {
		return TransactionEntity.builder()
				.token(transactionRequestDto.transactionToken())
				.operation(transactionRequestDto.operation())
				.amount(transactionRequestDto.inputPlayerAmount())
				.playerEntity(playerEntity)
				.build();
	}

	/**
	 * Converts Transaction entity to TransactionResponseDto.
	 *
	 * @return The TransactionRequestDto.
	 */
	@Mapping(target = "operation", source = "operation")
	@Mapping(target = "amount", source = "amount")
	@Mapping(target = "token", source = "token")
	TransactionResponseDto toResponseDto(TransactionEntity transactionEntity);
}
