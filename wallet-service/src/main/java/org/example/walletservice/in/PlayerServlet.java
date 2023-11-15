package org.example.walletservice.in;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthorizationResponse;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.PlayerRequestDto;
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
	 * @param playerRequest The PlayerRequestDto containing player registration information.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/registration")
	public ResponseEntity<String> registrationNewPlayer(@RequestBody PlayerRequestDto playerRequest) {
		playerService.registrationPlayer(playerRequest);
		return new ResponseEntity<>("You successfully registered.", HttpStatus.OK);
	}

	/**
	 * Handles the HTTP POST request to log in a player.
	 *
	 * @param playerRequest The PlayerRequestDto containing player login information.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/log_in")
	public ResponseEntity<AuthorizationResponse> logIn(@RequestBody PlayerRequestDto playerRequest) {
		String token = playerService.logIn(playerRequest);
		AuthorizationResponse authorizationResponse =
				new AuthorizationResponse(new Date().toString(), token, "You've successfully logged in");
		return new ResponseEntity<>(authorizationResponse, HttpStatus.OK);
	}

	/**
	 * Handles the HTTP GET request to retrieve the player balance.
	 *
	 * @param userDetails Authorized Player Data.
	 * @return ResponseEntity containing the BalanceResponseDto and HTTP status.
	 */
	@GetMapping("/balance")
	public ResponseEntity<?> getBalance(@AuthenticationPrincipal UserDetails userDetails) {
		BalanceResponseDto balanceResponse = playerService.getPlayerBalance(userDetails);
		return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
	}
}