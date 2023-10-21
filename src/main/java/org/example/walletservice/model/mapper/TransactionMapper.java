package org.example.walletservice.model.mapper;

import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.entity.Transaction;

public interface TransactionMapper {
	TransactionRequestDto toDto(Transaction entity);
	Transaction toEntity(TransactionRequestDto dto);
}
