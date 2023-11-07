package org.example.walletservice.service.impl;

import org.assertj.core.api.AssertionsForClassTypes;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.model.entity.Player;
import org.example.walletservice.model.mapper.BalanceMapper;
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
import org.junit.jupiter.api.DisplayName;
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
	private BalanceMapper balanceMapper;

	@BeforeEach
	public void setUp() {
		playerMapper = Mockito.mock(PlayerMapper.class);
		loggerService = Mockito.mock(LoggerService.class);
		balanceMapper = Mockito.mock(BalanceMapper.class);
		playerRepository = Mockito.mock(PlayerRepository.class);
		playerService = new PlayerServiceImpl(playerRepository, playerMapper, loggerService, balanceMapper);

		player = new Player();
		player.setPlayerID(1);
		player.setUsername("username");
		player.setPassword("password");
		player.setRole(Role.USER);

		playerRequest = new PlayerRequestDto("username", "password");
	}

	@Test
	@DisplayName("Must successfully register the user")
	void shouldRegistrationPlayer_successful() {
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.empty());
		Mockito.when(playerRepository.registrationPayer(Mockito.any(Player.class))).thenReturn(1);
		Mockito.when(playerMapper.toEntityFromRequest(playerRequest)).thenReturn(player);

		playerService.registrationPlayer(playerRequest);

		Mockito.verify(loggerService).recordActionInLog(Operation.REGISTRATION, player, Status.SUCCESSFUL);
	}

	@Test
	@DisplayName("Must not register the user because the name is already taken")
	public void shouldNotRegisteredPlayer_error() {
		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.of(player));

		Assertions.assertThrows(PlayerAlreadyExistException.class, () -> {
			playerService.registrationPlayer(playerRequest);
		});

		Mockito.verify(playerRepository, Mockito.never()).registrationPayer(Mockito.any(Player.class));
		Mockito.verify(playerMapper, Mockito.never()).toEntityFromRequest(playerRequest);
	}

	@Test
	@DisplayName("Must not register the user because the name is empty")
	public void shouldNotRegisteredPlayer_invalidUsernameEqNull() {
		final String message = "Username or password can't be empty.";
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
	@DisplayName("Doesn't have to register the user because the password is blank")
	public void shouldNotRegisteredPlayer_invalidPasswordEqNull() {
		final String message = "Username or password can't be empty.";
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
	@DisplayName("Must not register the user because the password is less than one password long")
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
	@DisplayName("")
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
	@DisplayName("")
	public void shouldNotRegistrationPlayer_emptyInputData() {
		final String message = "Username or password can't be empty.";

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.registrationPlayer(new PlayerRequestDto(null, null));
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	@DisplayName("Must be successfully logged in")
	public void shouldLogInPlayer_success() {
		AuthPlayerDto expected = new AuthPlayerDto(player.getPlayerID(), playerRequest.username(), player.getRole());

		Mockito.when(playerRepository.findPlayer(playerRequest.username())).thenReturn(Optional.of(player));
		Mockito.when(playerMapper.toAuthPlayerDto(player)).thenReturn(expected);

		AuthPlayerDto authPlayer = playerService.logIn(playerRequest);

		AssertionsForClassTypes.assertThat(expected).isEqualTo(authPlayer);
		Mockito.verify(playerRepository).findPlayer(playerRequest.username());
	}

	@Test
	@DisplayName("Must not be successful in logging in because no such username exists")
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
	@DisplayName("Shouldn't sign in because passwords don't matc")
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
	@DisplayName("Must not be logged in because the name is empty")
	public void shouldNotLogInPlayer_invalidUsernameEqNull() {
		final String message = "Username or password can't be empty.";
		playerRequest = new PlayerRequestDto(null, "password");

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	@DisplayName("The user does not have to log in to the user because the password is blank")
	public void shouldNotLogInPlayer_invalidPasswordEqNull() {
		final String message = "Username or password can't be empty.";
		playerRequest = new PlayerRequestDto("username", null);

		InvalidInputDataException exception = Assertions.assertThrows(InvalidInputDataException.class, () -> {
			playerService.logIn(playerRequest);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);

		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	@DisplayName("The user doesn't have to sign in because the password is less than one\n")
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
	@DisplayName("The user doesn't have to sign in because the name is less than one")
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
	@DisplayName("The user doesn't have to sign in because no data has been entered")
	public void shouldNotLogInPlayer_emptyInputData() {
		final String message = "Performing an operation by an unregistered user.";

		PlayerNotLoggedInException exception = Assertions.assertThrows(PlayerNotLoggedInException.class, () -> {
			playerService.logIn(null);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
		Mockito.verify(playerMapper, Mockito.never()).toAuthPlayerDto(Mockito.any(Player.class));
		Mockito.verify(playerRepository, Mockito.never()).findPlayer(playerRequest.username());
	}

	@Test
	@DisplayName("The user should not receive the balance as it is not authorized")
	public void shouldGetBalancePlayer_unauthorizedUser() {
		final String message = "Performing an operation by an unregistered user.";

		PlayerNotLoggedInException exception = Assertions.assertThrows(PlayerNotLoggedInException.class, () -> {
			playerService.getPlayerBalance(null);
		});

		AssertionsForClassTypes.assertThat(exception.getMessage()).isEqualTo(message);
	}

	@Test
	@DisplayName("The user should receive the balance")
	public void shouldGetBalancePlayer_successful() {
		AuthPlayerDto authPlayer = new AuthPlayerDto(1, "username", Role.USER);
		BalanceResponseDto balance = new BalanceResponseDto("admin", new BigDecimal(100));

		Mockito.when(playerMapper.toEntity(authPlayer)).thenReturn(player);
		Mockito.when(playerRepository.findPlayerBalance(player)).thenReturn(player);
		Mockito.when(balanceMapper.toDto(player.getUsername(), player.getBalance())).thenReturn(balance);

		BalanceResponseDto balanceResponseDto = playerService.getPlayerBalance(authPlayer);

		AssertionsForClassTypes.assertThat(balanceResponseDto).isEqualTo(balance);
	}
}