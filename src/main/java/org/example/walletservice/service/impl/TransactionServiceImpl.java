package org.example.walletservice.service.impl;

import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.entity.Transaction;
import org.example.walletservice.model.mapper.TransactionMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.repository.TransactionRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;

import java.math.BigDecimal;
import java.util.List;

public final class TransactionServiceImpl implements TransactionService {
	private final LoggerService loggerService;
	private final TransactionRepository transactionRepository;
	private final PlayerRepository playerRepository;
	private final TransactionMapper transactionMapper;
	private static final String CREDIT_SUCCESSFUL = "*Credit successfully.*\n";
	private static final String DEBIT_SUCCESSFUL = "*Debit successfully.*\n";
	private static final String FAIL_NOT_UNIQUE_TRANSACTION_TOKEN =
			"*{{FAIL}} A transaction with this number already exists!*\n";
	private static final String FAIL_NOT_ENOUGH_FUNDS_ON_THE_ACCOUNT =
			"*{{FAIL}} There are not enough funds in the account!*\n";
	private static final String ERROR_CONNECTION_DATABASE =
			"There is an error with the database. Try again later.";
	private static final String TRANSACTIONS_EMPTY =
					"""
					Transactions is empty.
					""";
	private static final String TRANSACTION_RECORD_TEMPLATE =
					"""
					                      - %s -
					\t- Transaction number: %s -
					\t- Your balance after transaction: %s -
					""";

	public TransactionServiceImpl(LoggerService loggerService, TransactionRepository transactionRepository,
								  PlayerRepository playerRepository, TransactionMapper transactionMapper) {
		this.loggerService = loggerService;
		this.transactionRepository = transactionRepository;
		this.playerRepository = playerRepository;
		this.transactionMapper = transactionMapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void credit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		BigDecimal inputPlayerAmount = transactionRequestDto.inputPlayerAmount();
		String transactionToken = transactionRequestDto.transactionToken();

		Player player = new Player();
		player.setPlayerID(authPlayerDto.playerID());
		player.setUsername(authPlayerDto.username());
		player.setRole(authPlayerDto.role());

		if (inputPlayerAmount.compareTo(BigDecimal.ZERO) >= 0.0 &&
				!transactionRepository.checkTokenExistence(transactionToken)) {

			BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);
			BigDecimal newPlayerBalance = playerBalance.add(inputPlayerAmount);

			Transaction transaction = createTransaction(transactionToken, Operation.CREDIT,
					inputPlayerAmount, player, newPlayerBalance);

			transactionRepository.creditOrDebit(transaction, newPlayerBalance);
			System.out.println(CREDIT_SUCCESSFUL);
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println(FAIL_NOT_UNIQUE_TRANSACTION_TOKEN);
			loggerService.recordActionInLog(Operation.CREDIT, player, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void debit(AuthPlayerDto authPlayerDto, TransactionRequestDto transactionRequestDto) {
		BigDecimal inputPlayerAmount = transactionRequestDto.inputPlayerAmount();
		String transactionToken = transactionRequestDto.transactionToken();

		Player player = new Player();
		player.setPlayerID(authPlayerDto.playerID());
		player.setUsername(authPlayerDto.username());
		player.setRole(authPlayerDto.role());

		if (inputPlayerAmount.compareTo(BigDecimal.ZERO) >= 0.0 &&
				!transactionRepository.checkTokenExistence(transactionToken)) {
			BigDecimal playerBalance = playerRepository.findPlayerBalanceByPlayer(player);

			BigDecimal newPlayerBalance = playerBalance.subtract(inputPlayerAmount);
			if (newPlayerBalance.compareTo(BigDecimal.ZERO) < 0.0) {
				System.out.println(FAIL_NOT_ENOUGH_FUNDS_ON_THE_ACCOUNT);
				loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
				return;
			}

			Transaction transaction = createTransaction(transactionToken, Operation.DEBIT,
					inputPlayerAmount, player, newPlayerBalance);

			transactionRepository.creditOrDebit(transaction, newPlayerBalance);
			System.out.println(DEBIT_SUCCESSFUL);
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.SUCCESSFUL);
		} else {
			System.out.println(FAIL_NOT_UNIQUE_TRANSACTION_TOKEN);
			loggerService.recordActionInLog(Operation.DEBIT, player, Status.FAIL);
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<String> getPlayerTransactionalHistory(AuthPlayerDto authPlayerDto) {
		Player player = new Player();
		player.setPlayerID(authPlayerDto.playerID());
		player.setUsername(authPlayerDto.username());
		player.setRole(authPlayerDto.role());

		List<String> playerTransactionalHistory =
				transactionRepository.findPlayerTransactionalHistoryByPlayer(player);

		if (playerTransactionalHistory == null) {
			System.out.println(ERROR_CONNECTION_DATABASE);
			return null;
		}

		if (playerTransactionalHistory.isEmpty()) {
			System.out.println(TRANSACTIONS_EMPTY);
			loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
			return null;
		}
		loggerService.recordActionInLog(Operation.TRANSACTIONAL_HISTORY, player, Status.SUCCESSFUL);
		return playerTransactionalHistory;
	}

	/**
	 * Creates a Transaction object based on the provided parameters.
	 *
	 * @param token             The unique identifier for the transaction.
	 * @param operation         The operation type (e.g., CREDIT, DEBIT).
	 * @param inputPlayerAmount The amount involved in the transaction.
	 * @param player            The Player associated with the transaction.
	 * @param newPlayerBalance  The new balance of the Player after the transaction.
	 * @return A Transaction object representing the transaction details.
	 */
	private Transaction createTransaction(String token, Operation operation, BigDecimal inputPlayerAmount,
										  Player player, BigDecimal newPlayerBalance) {

		Transaction transaction = new Transaction();
		transaction.setToken(token);
		transaction.setOperation(Operation.CREDIT.name());
		transaction.setAmount(inputPlayerAmount);
		transaction.setPlayerID(player.getPlayerID());
		transaction.setRecord(String.format(TRANSACTION_RECORD_TEMPLATE, operation.name(), token, newPlayerBalance));

		return transaction;
	}
}
