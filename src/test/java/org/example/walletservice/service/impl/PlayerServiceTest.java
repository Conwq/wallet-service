package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.PlayerMapper;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.PlayerService;
import org.example.walletservice.service.enums.Operation;
import org.example.walletservice.service.enums.Status;
import org.example.walletservice.service.exception.InvalidInputDataException;
import org.example.walletservice.service.exception.PlayerAlreadyExistException;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.example.walletservice.service.exception.PlayerNotLoggedInException;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.math.BigDecimal;
import java.util.Optional;

class PlayerServiceTest {
	private PlayerRepository playerRepository;
	private LoggerService loggerService;
	private PlayerService playerService;
	private Player player;
	private PlayerMapper playerMapper;
	private PlayerRequestDto playerRequest;

	@BeforeEach
	public void setUp() {
		playerMapper = Mockito.mock(PlayerMapper.class);
		loggerService = Mockito.mock(LoggerServiceImpl.class);
		playerRepository = Mockito.mock(PlayerRepository.class);
		playerService = new PlayerServiceImpl(playerRepository, loggerService, playerMapper);

		player = new Player();
		player.setPlayerID(1);
		player.setUsername("username");
		player.setPassword("password");
		player.setRole(Role.USER);

		playerRequest = new PlayerRequestDto("username", "password");
	}

	@Test
	void shouldRegistrationPlayer_successful() {
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.empty());
		Mockito.when(playerRepository.registrationPayer(Mockito.any(Player.class))).thenReturn(1);
		Mockito.when(playerMapper.toEntityFromRequest(playerRequest)).thenReturn(player);

		playerService.registrationPlayer(playerRequest);

		Mockito.verify(loggerService).recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	@Test
	public void shouldNotRegisteredPlayer_error() {
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.of(player));

		Assertions.assertThrows(PlayerAlreadyExistException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
	}

	@Test
	public void shouldNotRegisteredPlayer_invalidUsernameEqNull() {
		final String message = "Username or password can`t be empty.";
		playerRequest = new PlayerRequestDto(null, "password");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotRegisteredPlayer_invalidPasswordEqNull() {
		final String message = "Username or password can`t be empty.";
		playerRequest = new PlayerRequestDto("username", null);

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotRegisteredPlayer_invalidPasswordLessThanOneLength() {
		final String message = "The length of the username or password cannot be less than 1";
		playerRequest = new PlayerRequestDto("username", "");


		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotRegisteredPlayer_invalidUsernameLessThanOneLength() {
		final String message = "The length of the username or password cannot be less than 1";
		playerRequest = new PlayerRequestDto("", "password");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotRegistrationPlayer_emptyInputData() {
		final String message = "To log in, you need to enter your login and password";

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(null);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldLogInPlayer_success() {
		AuthPlayerDto expected = new AuthPlayerDto(player.getPlayerID(), playerRequest.username(), player.getRole());

		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.of(player));
		Mockito.when(playerMapper.toAuthPlayerDto(player)).thenReturn(expected);

		AuthPlayerDto authPlayer = playerService.logIn(playerRequest);

		AssertionsForClassTypes.assertThat(expected).isEqualTo(authPlayer);
		Mockito.verify(playerRepository).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_notFoundPlayer() {
		final String message = "Current player not found. Please try again.";
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.empty());

		PlayerNotFoundException exception = Assertions.assertThrows(PlayerNotFoundException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerRepository).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_PasswordsDontMatch() {
		final String message = "Incorrect password.";
		player.setPassword("different_password");
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.of(player));

		PlayerNotFoundException exception = Assertions.assertThrows(PlayerNotFoundException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerRepository).findPlayer(playerRequest.username());
		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(player);
	}

	@Test
	public void shouldNotLogInPlayer_invalidUsernameEqNull() {
		final String message = "Username or password can`t be empty.";
		playerRequest = new PlayerRequestDto(null, "password");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_invalidPasswordEqNull() {
		final String message = "Username or password can`t be empty.";
		playerRequest = new PlayerRequestDto("username", null);

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_invalidPasswordLessThanOneLength() {
		final String message = "The length of the username or password cannot be less than 1";
		playerRequest = new PlayerRequestDto("username", "");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_invalidUsernameLessThanOneLength() {
		final String message = "The length of the username or password cannot be less than 1";
		playerRequest = new PlayerRequestDto("", "password");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldNotLogInPlayer_emptyInputData() {
		final String message = "To log in, you need to enter your login and password";

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(null);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	public void shouldGetBalancePlayer_unauthorizedUser() {
		final String message = "Performing an operation by an unregistered user.";

		PlayerNotLoggedInException exception = Assertions.assertThrows(PlayerNotLoggedInException.class, () -> {
			playerService.getPlayerBalance(null);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	public void shouldGetBalancePlayer_successful() {
		AuthPlayerDto authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);
		BigDecimal balance = new BigDecimal(100);

		Mockito.when(playerMapper.toEntity(authPlayer)).thenReturn(player);
		Mockito.when(playerRepository.findPlayerBalanceByPlayer(player)).thenReturn(balance);

		BigDecimal playerBalance = playerService.getPlayerBalance(authPlayer);

		AssertionsForClassTypes.assertThat(playerBalance).isEqualTo(balance);
	}
}