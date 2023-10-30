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
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.example.walletservice.service.exception.TransactionNumberAlreadyExist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class TransactionServiceImpl implements TransactionService {
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;
	private final PlayerMapper playerMapper;
	private final LoggerService loggerService;

	@Autowired
	public TransactionServiceImpl(TransactionRepository transactionRepository,
								  PlayerRepository playerRepository,
								  LoggerService loggerService,
								  TransactionMapper transactionMapper,
								  PlayerMapper playerMapper) {
		this.transactionRepository = transactionRepository;
		this.playerRepository = playerRepository;
		this.transactionMapper = transactionMapper;
		this.playerMapper = playerMapper;
		this.loggerService = loggerService;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<TransactionResponseDto> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto) {
		Player player = userAuthorizationVerification(authPlayerDto);
		List<Transaction> playerTransactionalHistory = transactionRepository.findPlayerTransactionalHistory(player);
		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
		return playerTransactionalHistory.stream().map(transactionMapper::toDto).toList();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		Player player = userAuthorizationVerification(authPlayerDto);
		validatingInputData(player, transactionRequestDto);

		Player findPlayer = playerRepository.findPlayerBalance(player);

		BigDecimal playerBalance = findPlayer.getBalance();
		BigDecimal newPlayerBalance = playerBalance.add(transactionRequestDto.inputPlayerAmount());

		Transaction transaction = transactionMapper.toEntity(
				transactionRequestDto,
				player,
				Operation.CREDIT,
				newPlayerBalance
		);
		loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		Player player = userAuthorizationVerification(authPlayerDto);
		validatingInputData(player, transactionRequestDto);

		Player findPlayer = playerRepository.findPlayerBalance(player);

		BigDecimal playerBalance = findPlayer.getBalance();
		BigDecimal newPlayerBalance = playerBalance.subtract(transactionRequestDto.inputPlayerAmount());

		if (newPlayerBalance.subtract(BigDecimal.ZERO).compareTo(BigDecimal.ZERO) < 0) {
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
			throw new InvalidInputDataException("The number of funds to be withdrawn exceeds the number of funds on the account.");
		}

		Transaction transaction = transactionMapper.toEntity(
				transactionRequestDto,
				player,
				Operation.DEBIT,
				newPlayerBalance);

		loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
	}

	/**
	 * Validates the input data in the provided TransactionRequestDto.
	 *
	 * @param transactionRequestDto The TransactionRequestDto containing transaction information.
	 * @throws InvalidInputDataException If the input data is invalid, such as a negative amount or an existing transaction number.
	 */
	private void validatingInputData(Player player, TransactionRequestDto transactionRequestDto) {
		final String token = transactionRequestDto.transactionToken();
		final BigDecimal amount = transactionRequestDto.inputPlayerAmount();

		if (amount == null || token == null) {
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
			throw new InvalidInputDataException("Data can't be null");
		}
		if (amount.compareTo(BigDecimal.ZERO) < 0) {
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
			throw new InvalidInputDataException("The amount to be entered cannot be less than 0.");
		}
		if (transactionRepository.checkTokenExistence(token)) {
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
			throw new TransactionNumberAlreadyExist("A transaction with this number already exists.");
		}
	}

	/**
	 * Checks if the AuthPlayerDto is valid (not null).
	 *
	 * @param authPlayerDto The AuthPlayerDto to check.
	 * @throws PlayerDoesNotHaveAccessException If the AuthPlayerDto is null, indicating an unregistered user.
	 */
	private Player userAuthorizationVerification(AuthPlayerDto authPlayerDto) {
		if (authPlayerDto == null) {
			throw new PlayerDoesNotHaveAccessException("You need to log in. This resource is not available to you.");
		}
		return playerMapper.toEntity(authPlayerDto);
	}
}