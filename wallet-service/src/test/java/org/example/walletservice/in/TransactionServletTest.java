package org.example.walletservice.in;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.TransactionRequestDto;
import org.example.walletservice.model.dto.TransactionResponseDto;
import org.example.walletservice.service.TransactionService;
import org.example.walletservice.service.enums.Operation;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
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

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class TransactionServletTest {
	@MockBean
	private TransactionService transactionService;
	private final MockMvc mockMvc;
	private AuthPlayerDto authPlayer;

	@Autowired
	public TransactionServletTest(MockMvc mockMvc) {
		this.mockMvc = mockMvc;
	}

	@BeforeEach
	void setUp() {
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

		mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
						.contentType(MediaType.APPLICATION_JSON)
						.requestAttr("authPlayer", authPlayer))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(2)));
	}

	@Test
	@DisplayName("Should return empty transaction history")
	public void shouldReturnEmptyTransactionHistory() throws Exception {
		final List<TransactionResponseDto> transactionList = new ArrayList<>();
		Mockito.when(transactionService.getPlayerTransactionalHistory(authPlayer)).thenReturn(transactionList);

		mockMvc.perform(MockMvcRequestBuilders.get("/transaction")
						.contentType(MediaType.APPLICATION_JSON)
						.requestAttr("authPlayer", authPlayer))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", Matchers.hasSize(0)));
	}

	@Test
	@DisplayName("Must top up the user's balance with the specified amount")
	public void shouldCredit() throws Exception {
		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(new BigDecimal(100), "token");
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		mockMvc.perform(MockMvcRequestBuilders.post("/transaction/credit")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(transactionRequest))
						.requestAttr("authPlayer", authPlayerDto))
				.andExpect(status().isOk());

		Mockito.verify(transactionService).credit(authPlayerDto, transactionRequest);
	}

	@Test
	@DisplayName("Must withdraw money from the user's account")
	public void shouldDebit() throws Exception {
		TransactionRequestDto transactionRequest =
				new TransactionRequestDto(BigDecimal.ZERO, "token");
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);

		mockMvc.perform(MockMvcRequestBuilders.post("/transaction/debit")
						.contentType(MediaType.APPLICATION_JSON)
						.accept(MediaType.APPLICATION_JSON)
						.content(new ObjectMapper().writeValueAsString(transactionRequest))
						.requestAttr("authPlayer", authPlayerDto))
				.andExpect(status().isOk());

		Mockito.verify(transactionService).debit(authPlayerDto, transactionRequest);
	}
}