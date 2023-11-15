package org.example.walletservice.service.impl;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class TransactionServiceTest {
//	@MockBean
//	private TransactionRepository transactionRepository;
//	@MockBean
//	private PlayerRepository playerRepository;
//	@MockBean
//	private PlayerMapper playerMapper;
//	@MockBean
//	private TransactionMapper transactionMapper;
//	private final TransactionService transactionService;
//	private static final String TRANSACTIONAL_TOKEN = "transactional_token";
//	private Player player;
//	private AuthPlayer authPlayer;
//	private TransactionRequestDto transactionRequestDto;
//
//	@Autowired
//	public TransactionServiceTest(TransactionService transactionService) {
//		this.transactionService = transactionService;
//	}
//
//	@BeforeEach
//	public void setUp() {
//		player = new Player();
//		player.setPlayerID(1);
//		player.setUsername("admin");
//		player.setPassword("admin");
//		player.setBalance(BigDecimal.TEN);
//		player.setRole(Role.ADMIN);
//
//		authPlayer = new AuthPlayer(1, "admin", Role.ADMIN);
//		transactionRequestDto = new TransactionRequestDto(BigDecimal.TEN, "token");
//	}
//
//	@Test
//	@DisplayName("The user must top up the balance")
//	public void shouldCredit_successful() {
//		Transaction transaction = new Transaction();
//		player.setBalance(BigDecimal.ZERO);
//
//		when(playerMapper.toEntity(authPlayer)).thenReturn(player);
//		when(playerRepository.findPlayerBalance(player)).thenReturn(player);
//		when(transactionMapper.toEntity(transactionRequestDto, player, Operation.CREDIT, BigDecimal.TEN))
//				.thenReturn(transaction);
//
//		transactionService.credit(authPlayer, transactionRequestDto);
//
//		verify(transactionRepository).creditOrDebit(transaction, BigDecimal.TEN);
//	}
//
//	@Test
//	@DisplayName("The user does not have to top up the balance because he is not authorized")
//	public void shouldNotCredit_playerDoesNotHaveAccess() {
//		final String message = "You need to log in. This resource is not available to you.";
//		Mockito.when(transactionRepository.checkTokenExistence(TRANSACTIONAL_TOKEN)).thenReturn(false);
//
//		PlayerDoesNotHaveAccessException exception = Assertions.assertThrows(PlayerDoesNotHaveAccessException.class, () -> {
//			transactionService.credit(null, transactionRequestDto);
//		});
//
//		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
//	}
//
//	@Test
//	@DisplayName("The User does not have to deposit or withdraw from the account as the amount of funds he has deposited exceeds the amount of available funds in his account")
//	public void shouldThrowException_invalidInputData() {
//		final String message = "The amount to be entered cannot be less than 0.";
//		transactionRequestDto = new TransactionRequestDto(new BigDecimal(-100), TRANSACTIONAL_TOKEN);
//
//		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
//			transactionService.credit(authPlayer, transactionRequestDto);
//		});
//
//		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
//	}
//
//	@Test
//	@DisplayName("The user does not have to perform any operation as the token they have entered is not unique")
//	public void shouldThrowException_transactionTokenNotUnique() {
//		final String message = "A transaction with this number already exists.";
//
//		transactionRequestDto = new TransactionRequestDto(BigDecimal.ONE, TRANSACTIONAL_TOKEN);
//		Mockito.when(transactionRepository.checkTokenExistence(transactionRequestDto.transactionToken()))
//				.thenReturn(true);
//
//		TransactionNumberAlreadyExist exception = Assertions.assertThrows(TransactionNumberAlreadyExist.class, () -> {
//			transactionService.credit(authPlayer, transactionRequestDto);
//		});
//
//		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
//	}
//
//	@Test
//	@DisplayName("The user must have successfully withdrawn money from the account")
//	public void shouldDebit_successful() {
//		Transaction transaction = new Transaction();
//
//		when(playerMapper.toEntity(authPlayer)).thenReturn(player);
//		when(playerRepository.findPlayerBalance(player)).thenReturn(player);
//		when(transactionMapper.toEntity(transactionRequestDto, player, Operation.DEBIT, BigDecimal.ZERO))
//				.thenReturn(transaction);
//
//		transactionService.debit(authPlayer, transactionRequestDto);
//
//		verify(transactionRepository).creditOrDebit(transaction, BigDecimal.ZERO);
//	}
//
//	@Test
//	@DisplayName("The user must successfully retrieve their empty transaction history")
//	public void shouldReturnPlayerTransactionalHistory_emptyMap() {
//		List<TransactionResponseDto> testTransactionHistory = new ArrayList<>();
//
//		Mockito.when(transactionRepository.findPlayerTransactionalHistory(player))
//				.thenReturn(new ArrayList<>());
//		Mockito.when(playerMapper.toEntity(authPlayer)).thenReturn(player);
//
//		List<TransactionResponseDto> transactions = transactionService.getPlayerTransactionalHistory(authPlayer);
//		AssertionsForClassTypes.assertThat(transactions).isEqualTo(testTransactionHistory);
//	}
//
//	@Test
//	@DisplayName("The user must successfully retrieve their transaction history")
//	public void shouldReturnPlayerTransactionalHistory() {
//		List<Transaction> testTransactionHistory =
//				new ArrayList<>(Collections.singleton(new Transaction()));
//
//		Mockito.when(transactionRepository.findPlayerTransactionalHistory(player))
//				.thenReturn(testTransactionHistory);
//		Mockito.when(playerMapper.toEntity(authPlayer)).thenReturn(player);
//
//		List<TransactionResponseDto> transactions = transactionService.getPlayerTransactionalHistory(authPlayer);
//		AssertionsForClassTypes.assertThat(transactions.size()).isEqualTo(testTransactionHistory.size());
//	}
}