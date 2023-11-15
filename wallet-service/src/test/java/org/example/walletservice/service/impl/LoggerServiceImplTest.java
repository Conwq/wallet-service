package org.example.walletservice.service.impl;

import org.junit.jupiter.api.Disabled;
import org.springframework.boot.test.context.SpringBootTest;

@Disabled
@SpringBootTest
class LoggerServiceImplTest {
//	@MockBean
//	private LoggerRepository loggerRepository;
//	@MockBean
//	private PlayerRepository playerRepository;
//	private final LoggerService loggerService;
//	private LogMapper logMapper;
//	private Player player;
//	private AuthPlayer authPlayer;
//
//	@Autowired
//	public LoggerServiceImplTest(LoggerService loggerService) {
//		this.loggerService = loggerService;
//	}
//
//	@BeforeEach
//	void setUp() {
//		logMapper = Mappers.getMapper(LogMapper.class);
//
//		player = new Player();
//		player.setPlayerID(1);
//		player.setUsername("user123");
//		player.setPassword("2312");
//		player.setRole(Role.USER);
//
//		authPlayer = new AuthPlayer(1, "admin", Role.ADMIN);
//	}
//
//	@Test
//	@DisplayName("Should successfully return all logs ")
//	public void shouldReturnAllLogs_successful() {
////		Log log = new Log();
////		log.setLog("log #1");
////
////		List<Log> logs = Collections.singletonList(log);
////		Mockito.when(loggerRepository.findAllActivityRecords()).thenReturn(logs);
////
////		List<LogResponseDto> result = loggerService.getAllLogs(authPlayer);
//
////		assertEquals(logMapper.toDto(logs.get(0)), result.get(0));
//	}
//
//	@Test
//	@DisplayName("Must successfully return a specific player's logs")
//	public void shouldReturnPlayerLogs_successful() {
//		Log first = new Log();
//		first.setLog("log #1");
//		first.setPlayerID(player.getPlayerID());
//
//		Log second = new Log();
//		second.setLog("log #2");
//		second.setPlayerID(player.getPlayerID());
//
//		Mockito.when(playerRepository.findPlayer(Mockito.any(String.class))).
//				thenReturn(Optional.of(player));
//		Mockito.when(loggerRepository.findActivityRecordsForPlayer(player.getPlayerID()))
//				.thenReturn(new ArrayList<>(List.of(first, second)));
//
//		List<LogResponseDto> logsByUsername = loggerService.getLogsByUsername(authPlayer, player.getUsername());
//
//		Assertions.assertThat(logsByUsername)
//				.extracting(LogResponseDto::record)
//				.contains(first.getLog(), second.getLog());
//	}
//
//	@Test
//	@DisplayName("Should not show player logs as they have not been found")
//	public void shouldNotShowPlayerLogs_playerNotFound() {
//		String inputUsernameForSearch = "username";
//		Mockito.when(playerRepository.findPlayer(inputUsernameForSearch)).thenReturn(Optional.empty());
//
//		assertThrows(PlayerNotFoundException.class, () -> {
//			loggerService.getLogsByUsername(authPlayer, inputUsernameForSearch);
//		});
//	}
}