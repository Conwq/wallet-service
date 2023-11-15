package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
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
@RequiredArgsConstructor
public class LoggerServlet {
	private final LoggerService loggerService;

	/**
	 * Handles the HTTP GET request to retrieve all logs.
	 *
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/all_log")
	public ResponseEntity<List<LogResponseDto>> getAllLogs() {
		List<LogResponseDto> logList = loggerService.getAllLogs();
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP GET request to retrieve logs for a specific player.
	 *
	 * @param username   The username of the player.
	 * @return ResponseEntity containing the List of LogResponseDto and HTTP status.
	 */
	@GetMapping("/player_log")
	public ResponseEntity<List<LogResponseDto>> getPlayerLog(@RequestParam("username") String username) {
		List<LogResponseDto> logList = loggerService.getLogsByUsername(username);
		return new ResponseEntity<>(logList, HttpStatus.OK);
	}
}