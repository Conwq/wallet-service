package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.*;
import org.example.walletservice.service.PlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.Date;

/**
 * Controller class to perform player operations.
 */
@RestController
@RequestMapping("/players")
@RequiredArgsConstructor
public final class PlayerServlet {
	private final PlayerService playerService;

	/**
	 * Handles the HTTP POST request to register a new player.
	 *
	 * @param request The PlayerRequestDto containing player registration information.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/registration")
	public ResponseEntity<RegistrationResponse> registrationNewPlayer(@RequestBody PlayerRequest request) {
		playerService.registrationPlayer(request);
		RegistrationResponse response = new RegistrationResponse(new Date().toString(),
				"You successfully registered.");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP POST request to log in a player.
	 *
	 * @param request The PlayerRequestDto containing player login information.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/log_in")
	public ResponseEntity<AuthorizationResponse> logIn(@RequestBody PlayerRequest request) {
		AuthorizationResponse response = playerService.logIn(request);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP GET request to retrieve the player balance.
	 *
	 * @param userDetails Authorized Player Data.
	 * @return ResponseEntity containing the BalanceResponseDto and HTTP status.
	 */
	@GetMapping("/balance")
	public ResponseEntity<BalanceResponseDto> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
		BalanceResponseDto response = playerService.getPlayerBalance(userDetails);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}