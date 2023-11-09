package org.example.walletservice.in;

import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import ru.patseev.auditspringbootstarter.logger.model.Roles;

import java.util.ArrayList;
import java.util.List;

@SpringBootTest
@AutoConfigureMockMvc
class LoggerServletTest {
	@MockBean
	private static LoggerService loggerService;
	private final MockMvc mockMvc;
	private static final String AUTH_PLAYER = "authPlayer";

	@Autowired
	public LoggerServletTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@Test
	@DisplayName("Must return all logs")
	void shouldReturnAllLogs_success() throws Exception {
		List<LogResponseDto> logList = new ArrayList<>() {{
			add(new LogResponseDto("Log message 1"));
			add(new LogResponseDto("Log message 2"));
		}};
		AuthPlayer authPlayer = new AuthPlayer(1, "admin", Roles.ADMIN);

		Mockito.when(loggerService.getAllLogs(authPlayer)).thenReturn(logList);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/log/all_log")
						.requestAttr(AUTH_PLAYER, authPlayer)
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
		AuthPlayer authPlayer = new AuthPlayer(1, "admin", Roles.ADMIN);
		Mockito.when(loggerService.getLogsByUsername(authPlayer, "admin")).thenReturn(logList);

		mockMvc.perform(MockMvcRequestBuilders
						.get("/log/player_log")
						.param("username", "admin")
						.requestAttr(AUTH_PLAYER, authPlayer)
						.contentType(MediaType.APPLICATION_JSON))
				.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)))
				.andExpect(MockMvcResultMatchers.jsonPath("$[1].record", Matchers.is("Log message 2")));
	}
}