package org.example.walletservice.in;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

@RunWith(SpringJUnit4ClassRunner.class)
class LoggerServletTest {
	private static MockMvc mockMvc;
	private static LoggerService loggerService;
	private static final String AUTH_PLAYER = "authPlayer";

	@BeforeAll
	public static void setUp() {
		loggerService = Mockito.mock(LoggerService.class);
		LoggerServlet loggerServlet = new LoggerServlet(loggerService);
		mockMvc = MockMvcBuilders.standaloneSetup(loggerServlet).build();
	}

	@Test
	@DisplayName("Must return all logs")
	void shouldReturnAllLogs_success() throws Exception {
		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		Mockito.when(loggerService.getAllLogs(authPlayerDto)).thenReturn(logList);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/log/all_log")
						.requestAttr(AUTH_PLAYER, authPlayerDto)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].record", Matchers.is("Log message 2")));
	}

	@Test
	@DisplayName("Must return logs of a specific player")
	void shouldReturnPlayersLogs() throws Exception {
		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);
		Mockito.when(loggerService.getLogsByUsername(authPlayerDto, "admin")).thenReturn(logList);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/log/player_log")
						.param("username", "admin")
						.requestAttr(AUTH_PLAYER, authPlayerDto)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].record", Matchers.is("Log message 2")));
	}
}