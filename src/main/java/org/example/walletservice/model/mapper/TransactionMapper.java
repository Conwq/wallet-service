package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.service.enums.Operation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.math.BigDecimal;

@Mapper
public interface TransactionMapper {
	TransactionMapper instance = Mappers.getMapper(TransactionMapper.class);

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
		transaction.setOperation(Operation.CREDIT.name());
		transaction.setAmount(dto.inputPlayerAmount());
		transaction.setPlayerID(player.getPlayerID());
		transaction.setRecord(String.format(TRANSACTION_RECORD_TEMPLATE, operation.name(), dto.transactionToken(),
				dto.inputPlayerAmount(), newPlayerBalance));

		return transaction;
	}

	@Mapping(target = "inputPlayerAmount", source = "amount")
	@Mapping(target = "transactionToken", source = "token")
	TransactionRequestDto toDto(Transaction entity);

}
