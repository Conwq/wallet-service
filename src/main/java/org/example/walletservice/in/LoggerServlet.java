package org.example.walletservice.in;

import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Controller class to handle log-related operations.
 */
@RestController
@RequestMapping("/log")
public class LoggerServlet {
	private static final String AUTH_PLAYER = "authPlayer";
	private final LoggerService loggerService;

	public LoggerServlet(LoggerService loggerService) {
		this.loggerService = loggerService;
	}

	@GetMapping("/all_log")
	public ResponseEntity<List<LogResponseDto>> getAllLogs() {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);
		List<LogResponseDto> logList = loggerService.getAllLogs(authPlayerDto);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}

	@GetMapping("/player_log")
	public ResponseEntity<List<LogResponseDto>> getPlayerLog(@RequestParam("username") String username) {
		AuthPlayerDto authPlayerDto = new AuthPlayerDto(1, "admin", Role.ADMIN);
		List<LogResponseDto> logList = loggerService.getLogsByUsername(authPlayerDto, username);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}


//	protected void doGet(HttpServletRequest req,
//						 HttpServletResponse resp) throws ServletException, IOException {
//		try {
//			AuthPlayerDto authPlayerDto = (AuthPlayerDto) req.getAttribute(AUTH_PLAYER);
//			checkForData(authPlayerDto);
//			handleCommand(req, resp, authPlayerDto);
//
//		} catch (PlayerNotLoggedInException | PlayerDoesNotHaveAccessException e) {
//			generateResponse(resp, HttpServletResponse.SC_NOT_ACCEPTABLE, e.getMessage());
//
//	}
//


//	private void checkForData(AuthPlayerDto authPlayerDto) {
//		if (authPlayerDto == null) {
//			System.out.println("[FAIL] Performing an operation by an unregistered user.");
//			throw new PlayerNotLoggedInException("Only an authorized administrator can perform this operation.");
//		}
//
//		if (authPlayerDto.role() != Role.ADMIN) {
//			System.out.println("[FAIL] You do not have access to this resource.");
//			throw new PlayerDoesNotHaveAccessException("You do not have access to this resource.");
//		}
//	}
}