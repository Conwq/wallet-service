package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.ent.entity.PlayerEntity;
import org.example.walletservice.model.ent.entity.TransactionEntity;
import org.example.walletservice.model.mapper.TransactionMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
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
	@Transactional(readOnly = true)
	@Override
	public List<TransactionResponseDto> getPlayerTransactionalHistory(AuthPlayer authPlayer)
			throws PlayerDoesNotHaveAccessException {
		PlayerEntity playerEntity = passDataValidationAndGetPlayer(authPlayer);

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
	public void credit(AuthPlayer authPlayer, TransactionRequestDto transactionRequestDto)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist {

		PlayerEntity playerEntity = passDataValidationAndGetPlayer(authPlayer);
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
	public void debit(AuthPlayer authPlayer, TransactionRequestDto transactionRequestDto)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist {

		PlayerEntity playerEntity = passDataValidationAndGetPlayer(authPlayer);
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

	/**
	 * Checks if the AuthPlayer is valid (not null).
	 *
	 * @param authPlayer The AuthPlayer to check.
	 * @return The player associated with the AuthPlayer.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayer is null, indicating an unregistered user.
	 */
	private PlayerEntity passDataValidationAndGetPlayer(AuthPlayer authPlayer) throws PlayerDoesNotHaveAccessException {
		if (authPlayer == null) {
			throw new PlayerDoesNotHaveAccessException("You need to log in. This resource is not available to you.");
		}
		return playerRepository.findByUsername(authPlayer.username())
				.orElseThrow(() -> new PlayerNotFoundException("Current player not found. Please try again."));
	}
}