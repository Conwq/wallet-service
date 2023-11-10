package org.example.walletservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.model.enums.Operation;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.model.mapper.TransactionMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;
	private final PlayerMapper playerMapper;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TransactionResponseDto> getPlayerTransactionalHistory(AuthPlayer authPlayer)
			throws PlayerDoesNotHaveAccessException {
		Player player = userAuthorizationVerification(authPlayer);
		List<Transaction> playerTransactionalHistory = transactionRepository.findPlayerTransactionalHistory(player);
		return playerTransactionalHistory.stream().map(transactionMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(AuthPlayer authPlayer, TransactionRequestDto transactionRequestDto)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist {

		Player player = userAuthorizationVerification(authPlayer);
		validatingInputData(transactionRequestDto);

		Player findPlayer = playerRepository.findPlayerBalance(player);

		BigDecimal playerBalance = findPlayer.getBalance();
		BigDecimal newPlayerBalance = playerBalance.add(transactionRequestDto.inputPlayerAmount());

		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.CREDIT,
				newPlayerBalance);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(AuthPlayer authPlayer, TransactionRequestDto transactionRequestDto)
			throws PlayerDoesNotHaveAccessException, InvalidInputDataException, TransactionNumberAlreadyExist {

		Player player = userAuthorizationVerification(authPlayer);
		validatingInputData(transactionRequestDto);

		Player findPlayer = playerRepository.findPlayerBalance(player);

		BigDecimal playerBalance = findPlayer.getBalance();
		BigDecimal newPlayerBalance = playerBalance.subtract(transactionRequestDto.inputPlayerAmount());

		if (newPlayerBalance.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidInputDataException("The number of funds to be withdrawn exceeds the number of funds on the account.");
		}

		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.DEBIT,
				newPlayerBalance);

		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * Validates the input data in the provided TransactionRequestDto.
	 *
	 * @param transactionRequestDto The TransactionRequestDto containing transaction information.
	 * @throws InvalidInputDataException        If the input data is invalid, such as a negative amount or an existing transaction number.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayer is null, indicating an unregistered user.
	 */
	private void validatingInputData(TransactionRequestDto transactionRequestDto) {
		final String token = transactionRequestDto.transactionToken();
		final BigDecimal amount = transactionRequestDto.inputPlayerAmount();

		if (amount == null || token == null) {
			throw new InvalidInputDataException("Data can't be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			throw new InvalidInputDataException("The amount to be entered cannot be less than 0.");
		}
		if (transactionRepository.checkTokenExistence(token)) {
			throw new TransactionNumberAlreadyExist("A transaction with this number already exists.");
		}
	}

	/**
	 * Checks if the AuthPlayer is valid (not null).
	 *
	 * @param AuthPlayer The AuthPlayer to check.
	 * @return The player associated with the AuthPlayer.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayer is null, indicating an unregistered user.
	 */
	private Player userAuthorizationVerification(AuthPlayer AuthPlayer) {
		if (AuthPlayer == null) {
			throw new PlayerDoesNotHaveAccessException("You need to log in. This resource is not available to you.");
		}
		return playerMapper.toEntity(AuthPlayer);
	}
}