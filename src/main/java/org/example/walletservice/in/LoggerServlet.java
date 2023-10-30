package org.example.walletservice.in;

import jakarta.servlet.http.HttpServletRequest;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.example.walletservice.service.exception.PlayerDoesNotHaveAccessException;
import org.springframework.beans.factory.annotation.Autowired;
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

	/**
	 * Constructs a LoggerServlet with the provided LoggerService.
	 *
	 * @param loggerService The LoggerService used to perform log-related operations.
	 */
	@Autowired
	public LoggerServlet(LoggerService loggerService) {
		this.loggerService = loggerService;
	}

	/**
	 * Handles the HTTP GET request to retrieve all logs.
	 *
	 * @param request The HttpServletRequest object.
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/all_log")
	public ResponseEntity<List<LogResponseDto>> getAllLogs(HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);
		validateAdminAccess(authPlayerDto);
		List<LogResponseDto> logList = loggerService.getAllLogs(authPlayerDto);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP GET request to retrieve logs for a specific player.
	 *
	 * @param username The username of the player.
	 * @param request  The HttpServletRequest object.
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/player_log")
	public ResponseEntity<List<LogResponseDto>> getPlayerLog(@RequestParam("username") String username,
															 HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);
		validateAdminAccess(authPlayerDto);
		List<LogResponseDto> logList = loggerService.getLogsByUsername(authPlayerDto, username);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}

	/**
	 * Validates that the authenticated player has admin access.
	 *
	 * @param authPlayerDto The AuthPlayerDto containing player information.
	 * @throws PlayerDoesNotHaveAccessException If the player does not have admin access.
	 */
	private void validateAdminAccess(AuthPlayerDto authPlayerDto) {
		if (authPlayerDto == null || authPlayerDto.role() != Role.ADMIN) {
			throw new PlayerDoesNotHaveAccessException("You do not have access to this resource.");
		}
	}
}
