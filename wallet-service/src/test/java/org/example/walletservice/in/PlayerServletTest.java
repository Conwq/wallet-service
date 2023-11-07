package org.example.walletservice.in;

import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectMapper;
import org.testcontainers.shaded.com.fasterxml.jackson.databind.ObjectWriter;

import java.math.BigDecimal;
import java.util.HashMap;

@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@AutoConfigureMockMvc
class PlayerServletTest {
	private static PlayerService playerService;
	private static JwtService jwtService;
	private static AuthPlayerDto authPlayer;
	private static ObjectWriter objectWriter;
	private static MockMvc mockMvc;
	private static final String AUTH_PLAYER = "authPlayer";


	@BeforeAll
	public static void setUp() {
		playerService = Mockito.mock(PlayerService.class);
		jwtService = Mockito.mock(JwtService.class);
		authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);

		PlayerServlet playerServlet = new PlayerServlet(playerService, jwtService);
		mockMvc = MockMvcBuilders.standaloneSetup(playerServlet).build();
		ObjectMapper objectMapper = new ObjectMapper();
		objectWriter = objectMapper.writer();
	}

	@Test
	@DisplayName("Should show player balance")
	void shouldShowPlayerBalance() throws Exception {
		final BigDecimal balance = new BigDecimal(100);

		BalanceResponseDto balanceResponse = new BalanceResponseDto(authPlayer.username(), balance);
		Mockito.when(playerService.getPlayerBalance(authPlayer)).thenReturn(balanceResponse);

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
		PlayerRequestDto playerRequest = new PlayerRequestDto("username", "password");

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post("/players/registration")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsBytes(playerRequest));

		ResultActions perform = mockMvc.perform(request);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
		Mockito.verify(playerService).registrationPlayer(playerRequest);
	}

	@Test
	@DisplayName("Should perform sign-in operation")
	public void shouldPerformSigInOperation() throws Exception {
		PlayerRequestDto playerRequest = new PlayerRequestDto("user", "user123");
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "user", Role.USER);
		String jwtToken = "jwt_token";

		Mockito.when(playerService.logIn(playerRequest)).thenReturn(authPlayerDto);
		Mockito.when(jwtService.generateWebToken(new HashMap<>(), authPlayerDto)).thenReturn(jwtToken);

		MockHttpServletRequestBuilder request = MockMvcRequestBuilders
				.post("/players/log_in")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsBytes(playerRequest));

		ResultActions perform = mockMvc.perform(request);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
	}
}