package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
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
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class TransactionServiceTest {
	private LoggerServiceImpl loggerService;
	private TransactionRepository transactionRepository;
	private PlayerRepository playerRepository;
	private TransactionService transactionService;
	private TransactionMapper transactionMapper;
	private PlayerMapper playerMapper;
	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
	private Player player;
	private AuthPlayerDto authPlayerDto;
	private TransactionRequestDto transactionRequestDto;

	@BeforeEach
	public void setUp() {
		loggerService = Mockito.mock(LoggerServiceImpl.class);
		transactionRepository = Mockito.mock(TransactionRepository.class);
		playerRepository = Mockito.mock(PlayerRepository.class);
		playerRepository = Mockito.mock(PlayerRepository.class);
		transactionMapper = Mockito.mock(TransactionMapper.class);
		playerMapper = Mockito.mock(PlayerMapper.class);

		transactionService = new TransactionServiceImpl(transactionRepository,
				playerRepository,
				transactionMapper,
				playerMapper);

		player = new Player();
		player.setPlayerID(1);
		player.setUsername("admin");
		player.setPassword("admin");
		player.setBalance(BigDecimal.TEN);
		player.setRole(Role.ADMIN);

		authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);
		transactionRequestDto = new TransactionRequestDto(BigDecimal.TEN, "token");
	}

	@Test
	public void shouldCredit_successful() {
		Transaction transaction = new Transaction();

		when(playerMapper.toEntity(authPlayerDto)).thenReturn(player);
		when(playerRepository.findPlayerBalanceByPlayer(player)).thenReturn(BigDecimal.ZERO);
		when(transactionMapper.toEntity(transactionRequestDto, player, Operation.CREDIT, BigDecimal.TEN))
				.thenReturn(transaction);

		transactionService.credit(authPlayerDto, transactionRequestDto);

		verify(transactionRepository).creditOrDebit(transaction, BigDecimal.TEN);
	}

	@Test
	public void shouldNotCredit_playerDoesNotHaveAccess() {
		final String message = "You need to log in. This resource is not available to you.";

		when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);

		PlayerDoesNotHaveAccessException exception = Assertions.assertThrows(PlayerDoesNotHaveAccessException.class, () -> {
			transactionService.credit(null, transactionRequestDto);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	public void shouldThrowException_invalidInputData() {
		final String message = "The amount to be entered cannot be less than 0.";
		transactionRequestDto = new TransactionRequestDto(new BigDecimal(-100), TRANSACTIONAL_TOKEN);

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			transactionService.credit(authPlayerDto, transactionRequestDto);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	public void shouldThrowException_transactionTokenNotUnique() {
		final String message = "A transaction with this number already exists.";

		transactionRequestDto = new TransactionRequestDto(BigDecimal.ONE, TRANSACTIONAL_TOKEN);
		when(transactionRepository.checkTokenExistence(transactionRequestDto.transactionToken()))
				.thenReturn(true);

		TransactionNumberAlreadyExist exception = Assertions.assertThrows(TransactionNumberAlreadyExist.class, () -> {
			transactionService.credit(authPlayerDto, transactionRequestDto);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	public void shouldDebit_successful() {
		Transaction transaction = new Transaction();

		when(playerMapper.toEntity(authPlayerDto)).thenReturn(player);
		when(playerRepository.findPlayerBalanceByPlayer(player)).thenReturn(BigDecimal.TEN);
		when(transactionMapper.toEntity(transactionRequestDto, player, Operation.DEBIT, BigDecimal.ZERO))
				.thenReturn(transaction);

		transactionService.debit(authPlayerDto, transactionRequestDto);

		verify(transactionRepository).creditOrDebit(transaction, BigDecimal.ZERO);
	}

	@Test
	public void shouldGetPlayerTransactionalHistory_successful() {
		when(playerMapper.toEntity(authPlayerDto)).thenReturn(player);

		List<Transaction> transactions = new ArrayList<>();
		transactions.add(new Transaction());
		when(transactionRepository.findPlayerTransactionalHistoryByPlayer(player)).thenReturn(transactions);

		List<TransactionResponseDto> result = transactionService.getPlayerTransactionalHistory(authPlayerDto);

		assertNotNull(result);
		assertEquals(transactions.size(), result.size());
	}

	@Test
	public void shouldReturnPlayerTransactionalHistory_emptyMap() {
		List<TransactionResponseDto> testTransactionHistory = new ArrayList<>();

		Mockito.when(transactionRepository.findPlayerTransactionalHistoryByPlayer(player))
				.thenReturn(new ArrayList<>());
		Mockito.when(playerMapper.toEntity(authPlayerDto)).thenReturn(player);

		List<TransactionResponseDto> transactions = transactionService.getPlayerTransactionalHistory(authPlayerDto);
		AssertionsForClassTypes.assertThat(transactions).isEqualTo(testTransactionHistory);
	}

	@Test
	public void shouldReturnPlayerTransactionalHistory() {
		List<Transaction> testTransactionHistory =
				new ArrayList<>(Collections.singleton(new Transaction()));

		when(transactionRepository.findPlayerTransactionalHistoryByPlayer(player))
				.thenReturn(testTransactionHistory);
		when(playerMapper.toEntity(authPlayerDto)).thenReturn(player);

		List<TransactionResponseDto> transactions = transactionService.getPlayerTransactionalHistory(authPlayerDto);
		AssertionsForClassTypes.assertThat(transactions.size()).isEqualTo(testTransactionHistory.size());
	}
}