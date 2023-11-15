package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.entity.PlayerEntity;
import org.example.walletservice.model.entity.TransactionEntity;
import org.example.walletservice.model.mapper.TransactionMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public List<TransactionResponseDto> getPlayerTransactionalHistory(UserDetails userDetails)
			throws PlayerDoesNotHaveAccessException {
		PlayerEntity playerEntity = playerRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));

		return playerEntity.getListTransactionEntity()
				.stream()
				.map(transactionMapper::toResponseDto)
				.collect(Collectors.toList());
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void credit(UserDetails userDetails, TransactionRequestDto transactionRequestDto)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist {

		PlayerEntity playerEntity = playerRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));
		validatingInputData(transactionRequestDto);

		BigDecimal playerBalance = playerEntity.getBalance();
		BigDecimal newPlayerBalance = playerBalance.add(transactionRequestDto.inputPlayerAmount());
		playerEntity.setBalance(newPlayerBalance);

		TransactionEntity transactionEntity = transactionMapper.toEntity(transactionRequestDto, playerEntity);
		playerEntity.getListTransactionEntity().add(transactionEntity);

		transactionRepository.save(transactionEntity);
	}

	/**
	 * {@inheritDoc}
	 */
	@Transactional
	@Override
	public void debit(UserDetails userDetails, TransactionRequestDto transactionRequestDto)
			throws InvalidInputDataException, TransactionNumberAlreadyExist {

		PlayerEntity playerEntity = playerRepository.findByUsername(userDetails.getUsername())
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));
		validatingInputData(transactionRequestDto);

		BigDecimal playerBalance = playerEntity.getBalance();
		BigDecimal newPlayerBalance = playerBalance.subtract(transactionRequestDto.inputPlayerAmount());
		if (newPlayerBalance.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidInputDataException("The number of funds to be withdrawn exceeds the number of funds on the account.");
		}
		playerEntity.setBalance(newPlayerBalance);

		TransactionEntity transactionEntity = transactionMapper.toEntity(transactionRequestDto, playerEntity);
		playerEntity.getListTransactionEntity().add(transactionEntity);

		transactionRepository.save(transactionEntity);
	}

	/**
	 * Validates the input data in the provided TransactionRequestDto.
	 *
	 * @param transactionRequestDto The TransactionRequestDto containing transaction information.
	 * @throws InvalidInputDataException        If the input data is invalid, such as a negative amount or an existing transaction number.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayer is null, indicating an unregistered user.
	 */
	private void validatingInputData(TransactionRequestDto transactionRequestDto)
			throws InvalidInputDataException, TransactionNumberAlreadyExist {
		final String token = transactionRequestDto.transactionToken();
		final BigDecimal amount = transactionRequestDto.inputPlayerAmount();

		if (amount == null || token == null) {
			throw new InvalidInputDataException("Data can't be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidInputDataException("The amount to be entered cannot be less than 0.");
		}
		if (transactionRepository.existsTransactionEntityByToken(token)) {
			throw new TransactionNumberAlreadyExist("A transaction with this number already exists.");
		}
	}
}