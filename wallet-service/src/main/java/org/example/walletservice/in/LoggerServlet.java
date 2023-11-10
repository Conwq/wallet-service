package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.LogResponseDto;
import org.example.walletservice.service.LoggerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller class to handle log-related operations.
 */
@RestController
@RequestMapping("/log")
@RequiredArgsConstructor
public class LoggerServlet {
	private final LoggerService loggerService;

	/**
	 * Handles the HTTP GET request to retrieve all logs.
	 *
	 * @param authPlayer Authorized player data.
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/all_log")
	public ResponseEntity<List<LogResponseDto>> getAllLogs(@RequestAttribute(required = false) AuthPlayer authPlayer) {
		List<LogResponseDto> logList = loggerService.getAllLogs(authPlayer);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP GET request to retrieve logs for a specific player.
	 *
	 * @param username   The username of the player.
	 * @param authPlayer Authorized player data.
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/player_log")
	public ResponseEntity<List<LogResponseDto>> getPlayerLog(@RequestParam("username") String username,
															 @RequestAttribute(required = false) AuthPlayer authPlayer) {
		List<LogResponseDto> logList = loggerService.getLogsByUsername(authPlayer, username);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}
}
