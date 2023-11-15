package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.enums.Role;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequest;
import org.example.walletservice.service.PlayerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.math.BigDecimal;

@Disabled
@SpringBootTest
@AutoConfigureMockMvc
class PlayerServletTest {
	@MockBean
	private PlayerService playerService;
	@MockBean
	private JwtService jwtService;
	private final MockMvc mockMvc;
	private final ObjectMapper objectMapper;
	private AuthPlayer authPlayer;
	private static final String AUTH_PLAYER = "authPlayer";

	@Autowired
	public PlayerServletTest(MockMvc mockMvc, ObjectMapper objectMapper) {
		this.mockMvc = mockMvc;
		this.objectMapper = objectMapper;
	}

	@BeforeEach
	public void setUp() {
		authPlayer = new AuthPlayer(1, "admin", Role.ADMIN);
	}

	@Test
	@DisplayName("Should show player balance")
	void shouldShowPlayerBalance() throws Exception {
		final BigDecimal balance = new BigDecimal(100);

		BalanceResponseDto balanceResponse = new BalanceResponseDto(authPlayer.username(), balance);
//		Mockito.when(playerService.getPlayerBalance(authPlayer)).thenReturn(balanceResponse);

		ResultActions perform = mockMvc.perform(MockMvcRequestBuilders
				.get("/players/balance")
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr(AUTH_PLAYER, authPlayer));

		perform.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$.username", Matchers.is(authPlayer.username())));
	}

	@Test
	@DisplayName("Should register player")
	public void shouldRegisterPlayer() throws Exception {
		PlayerRequest playerRequest = new PlayerRequest("username", "password");

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post("/players/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(playerRequest));

		ResultActions perform = mockMvc.perform(request);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
		Mockito.verify(playerService).registrationPlayer(playerRequest);
	}

	@Test
	@DisplayName("Should perform sign-in operation")
	public void shouldPerformSigInOperation() throws Exception {
		PlayerRequest playerRequest = new PlayerRequest("user", "user123");
		AuthPlayer newAuthPlayer = new AuthPlayer(1, "user", Role.USER);
		String jwtToken = "jwt_token";

//		Mockito.when(playerService.logIn(playerRequest)).thenReturn(newAuthPlayer);
//		Mockito.when(jwtService.generateWebToken(new HashMap<>(), newAuthPlayer)).thenReturn(jwtToken);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post("/players/log_in")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsBytes(playerRequest));

		ResultActions perform = mockMvc.perform(request);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
	}
}