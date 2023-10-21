package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.entity.Transaction;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface TransactionMapper {
	TransactionMapper instance = Mappers.getMapper(TransactionMapper.class);

	@Mapping(target = "inputPlayerAmount", source = "amount")
	@Mapping(target = "transactionToken", source = "token")
	TransactionRequestDto toDto(Transaction entity);

	@Mapping(target = "amount", source = "inputPlayerAmount")
	@Mapping(target = "token", source = "transactionToken")
	Transaction toEntity(TransactionRequestDto dto);
}
