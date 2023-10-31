package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.model.mapper.TransactionMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;

import java.math.BigDecimal;
import java.util.List;

public final class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;
	private final PlayerMapper playerMapper;

	public TransactionServiceImpl(TransactionRepository transactionRepository,
								  PlayerRepository playerRepository, TransactionMapper transactionMapper,
								  PlayerMapper playerMapper) {
		this.transactionRepository = transactionRepository;
		this.playerRepository = playerRepository;
		this.transactionMapper = transactionMapper;
		this.playerMapper = playerMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		validatingInputData(transactionRequestDto);
		Player player = checkForData(authPlayerDto);

		BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);
		BigDecimal newPlayerBalance = playerBalance.add(transactionRequestDto.inputPlayerAmount());
		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.CREDIT,
				newPlayerBalance);

		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		validatingInputData(transactionRequestDto);

		Player player = checkForData(authPlayerDto);
		BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);
		BigDecimal newPlayerBalance = playerBalance.subtract(transactionRequestDto.inputPlayerAmount());

		if (newPlayerBalance.compareTo(BigDecimal.ZERO) < 0.0) {
			throw new InvalidInputDataException(
					"The number of funds to be withdrawn exceeds the number of funds on the account.");
		}

		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.DEBIT,
				newPlayerBalance);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TransactionResponseDto> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto) {
		Player player = checkForData(authPlayerDto);

		List<Transaction> playerTransactionalHistory = transactionRepository.findPlayerTransactionalHistoryByPlayer(player);

		if (playerTransactionalHistory == null) {
			System.out.println("[FAIL] Database error.");
			return null;
		}
		return playerTransactionalHistory.stream().map(transactionMapper::toDto).toList();
	}

	/**
	 * Validates the input data in the provided TransactionRequestDto.
	 *
	 * @param transactionRequestDto The TransactionRequestDto containing transaction information.
	 * @throws InvalidInputDataException If the input data is invalid, such as a negative amount or an existing transaction number.
	 */
	private void validatingInputData(TransactionRequestDto transactionRequestDto) {
		if (transactionRequestDto.inputPlayerAmount().compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidInputDataException("The amount to be entered cannot be less than 0.");
		}
		if (transactionRepository.checkTokenExistence(transactionRequestDto.transactionToken())) {
			throw new TransactionNumberAlreadyExist("A transaction with this number already exists.");
		}
	}

	/**
	 * Checks if the AuthPlayerDto is valid (not null).
	 *
	 * @param authPlayerDto The AuthPlayerDto to check.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayerDto is null, indicating an unregistered user.
	 */
	private Player checkForData(AuthPlayerDto authPlayerDto) {
		if (authPlayerDto == null) {
			throw new PlayerDoesNotHaveAccessException("You need to log in. This resource is not available to you.");
		}
		return playerMapper.toEntity(authPlayerDto);
	}
}