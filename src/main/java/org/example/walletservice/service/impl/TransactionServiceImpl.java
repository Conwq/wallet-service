package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public final class TransactionServiceImpl implements TransactionService {
	private final LoggerService loggerService;
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;
	private final PlayerMapper playerMapper;
	private static final String CREDIT_SUCCESSFUL = "Credit successfully.\n";
	private static final String DEBIT_SUCCESSFUL = "Debit successfully.\n";
	private static final String FAIL_NOT_ENOUGH_FUNDS_ON_THE_ACCOUNT =
			"There are not enough funds in the account!\n";
	private static final String TRANSACTIONS_EMPTY =
			"""
					Transactions is empty.
					""";

	public TransactionServiceImpl(LoggerService loggerService, TransactionRepository transactionRepository,
								  PlayerRepository playerRepository, TransactionMapper transactionMapper,
								  PlayerMapper playerMapper) {
		this.loggerService = loggerService;
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
		Player player = playerMapper.toEntity(authPlayerDto);
		validatingInputData(player, transactionRequestDto, Operation.CREDIT);

		BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);
		BigDecimal newPlayerBalance = playerBalance.add(transactionRequestDto.inputPlayerAmount());
		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.CREDIT,
				newPlayerBalance);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
		System.out.println("[SUCCESSFUL] Credit successful.");
		loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		Player player = playerMapper.toEntity(authPlayerDto);
		validatingInputData(player, transactionRequestDto, Operation.DEBIT);

		BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);
		BigDecimal newPlayerBalance = playerBalance.subtract(transactionRequestDto.inputPlayerAmount());
		if (newPlayerBalance.compareTo(BigDecimal.ZERO) < 0.0) {
			System.out.println("[FAIL] Withdrawal error - insufficient funds in the account.");
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
			return;
		}
		Transaction transaction = transactionMapper.toEntity(transactionRequestDto, player, Operation.DEBIT,
				newPlayerBalance);
		transactionRepository.creditOrDebit(transaction, newPlayerBalance);
		System.out.println("[SUCCESSFUL] Debit successful.");
		loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto) {
		Player player = playerMapper.toEntity(authPlayerDto);
		List<String> playerTransactionalHistory = transactionRepository.findPlayerTransactionalHistoryByPlayer(player);
		if (playerTransactionalHistory == null) {
			System.out.println("[FAIL] Database error.");
			return null;
		}
		if (playerTransactionalHistory.isEmpty()) {
			System.out.println("[SUCCESSFUL] Transaction history has been viewed");
			loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
			return new ArrayList<>(List.of(TRANSACTIONS_EMPTY));
		}
		System.out.println("[SUCCESSFUL] Transaction history has been viewed");
		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
		return playerTransactionalHistory;
	}

	private void validatingInputData(Player player, TransactionRequestDto transactionRequestDto, Operation operation) {
		if (transactionRequestDto.inputPlayerAmount().compareTo(BigDecimal.ZERO) < 0) {
			System.out.println("[FAIL] The amount to be entered cannot be less than 0.");
			loggerService.recordActionInLog(operation, player, Status.FAIL);
			throw new InvalidInputDataException("The amount to be entered cannot be less than 0.");
		}
		if (transactionRepository.checkTokenExistence(transactionRequestDto.transactionToken())) {
			System.out.println("[FAIL] A transaction with this number already exists.");
			loggerService.recordActionInLog(operation, player, Status.FAIL);
			throw new InvalidInputDataException("A transaction with this number already exists.");
		}
	}
}