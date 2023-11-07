package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@RunWith(MockitoJUnitRunner.class)
class TransactionServletTest {
	private static ObjectWriter objectWriter;
	private static TransactionService transactionService;
	private static final String AUTH_PLAYER = "authPlayer";
	private static AuthPlayerDto authPlayer;
	private static MockMvc mockMvc;

	@BeforeAll
	static void setUp() {
		ObjectMapper objectMapper = new ObjectMapper();
		objectWriter = objectMapper.writer();
		transactionService = Mockito.mock(TransactionService.class);
		TransactionServlet transactionServlet = new TransactionServlet(transactionService);

		mockMvc = MockMvcBuilders.standaloneSetup(transactionServlet).build();

		authPlayer = new AuthPlayerDto(1, "admin", Role.ADMIN);
	}

	@Test
	@DisplayName("Should return transaction history")
	public void shouldReturnTransactionHistory() throws Exception {
		final List<TransactionResponseDto> transactionList = new ArrayList<>() {{
			add(new TransactionResponseDto(Operation.CREDIT.name(), new BigDecimal(100), "token #1"));
			add(new TransactionResponseDto(Operation.DEBIT.name(), new BigDecimal(20), "token #2"));
		}};
		Mockito.when(transactionService.getPlayerTransactionalHistory(authPlayer)).thenReturn(transactionList);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr(AUTH_PLAYER, authPlayer);

		ResultActions perform = mockMvc.perform(requestBuilder);

		perform.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(2)));
	}

	@Test
	@DisplayName("Should return empty transaction history")
	public void shouldReturnEmptyTransactionHistory() throws Exception {
		final List<TransactionResponseDto> transactionList = new ArrayList<>();
		Mockito.when(transactionService.getPlayerTransactionalHistory(authPlayer)).thenReturn(transactionList);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.get("/transaction")
				.contentType(MediaType.APPLICATION_JSON)
				.requestAttr(AUTH_PLAYER, authPlayer);

		ResultActions perform = mockMvc.perform(requestBuilder);

		perform.andExpect(MockMvcResultMatchers.status().isOk())
				.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasSize(0)));
	}

	@Test
	@DisplayName("Must top up the user's balance with the specified amount")
	public void shouldCredit() throws Exception {
		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/transaction/credit")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(transactionRequest))
				.requestAttr(AUTH_PLAYER, authPlayerDto);

		ResultActions perform = mockMvc.perform(requestBuilder);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
		Mockito.verify(transactionService).credit(authPlayerDto, transactionRequest);
	}

	@Test
	@DisplayName("Must withdraw money from the user's account")
	public void shouldDebit() throws Exception {
		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(BigDecimal.ZERO, "token");
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
				.post("/transaction/debit")
				.contentType(MediaType.APPLICATION_JSON)
				.accept(MediaType.APPLICATION_JSON)
				.content(objectWriter.writeValueAsString(transactionRequest))
				.requestAttr(AUTH_PLAYER, authPlayerDto);

		ResultActions perform = mockMvc.perform(requestBuilder);

		perform.andExpect(MockMvcResultMatchers.status().isOk());
		Mockito.verify(transactionService).debit(authPlayerDto, transactionRequest);
	}
}