package org.example.walletservice.in;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Controller class to handle log-related operations.
 */
@Api(description = "Endpoints for logging operations")
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
	@ApiOperation("Get a list of all logs for the authenticated player")
	@GetMapping("/all_log")
	public ResponseEntity<List<LogResponseDto>> getAllLogs(HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);
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
	@ApiOperation("Get logs for a specific player")
	@GetMapping("/player_log")
	public ResponseEntity<List<LogResponseDto>> getPlayerLog(@RequestParam("username") String username,
															 HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);
		List<LogResponseDto> logList = loggerService.getLogsByUsername(authPlayerDto, username);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}
}
