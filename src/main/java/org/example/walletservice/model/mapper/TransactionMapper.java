package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.service.enums.Operation;
import org.mapstruct.Mapper;

import java.math.BigDecimal;

/**
 * Mapper for converting between Transaction entity and TransactionRequestDto.
 */
@Mapper(componentModel = "spring")
public interface TransactionMapper {

	/**
	 * Converts TransactionRequestDto, Player entity, Operation, and new player balance to a Transaction entity.
	 *
	 * @param dto              The TransactionRequestDto.
	 * @param player           The Player entity.
	 * @param operation        The Operation type.
	 * @param newPlayerBalance The new player balance after the transaction.
	 * @return The Transaction entity.
	 */
	default Transaction toEntity(TransactionRequestDto dto, Player player, Operation operation, BigDecimal newPlayerBalance) {
		String TRANSACTION_RECORD_TEMPLATE =
				"""
											  - %s -
						- Transaction number: %s -
						- Transaction amount: %s -
						- Your balance after transaction: %s -
						""";

		Transaction transaction = new Transaction();
		transaction.setToken(dto.transactionToken());
		transaction.setOperation(operation.name());
		transaction.setAmount(dto.inputPlayerAmount());
		transaction.setPlayerID(player.getPlayerID());
		transaction.setRecord(String.format(TRANSACTION_RECORD_TEMPLATE, operation.name(), dto.transactionToken(),
				dto.inputPlayerAmount(), newPlayerBalance));

		return transaction;
	}


	/**
	 * Converts Transaction entity to TransactionResponseDto.
	 *
	 * @param entity The Transaction entity.
	 * @return The TransactionRequestDto.
	 */
	TransactionResponseDto toDto(Transaction entity);
}
