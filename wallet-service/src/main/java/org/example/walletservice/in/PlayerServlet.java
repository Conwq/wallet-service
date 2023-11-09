package org.example.walletservice.in;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class to perform player operations.
 */
@RestController
@RequestMapping("/players")
public final class PlayerServlet {
	private final PlayerService playerService;
	private final JwtService jwtService;

	@Autowired
	public PlayerServlet(PlayerService playerService,
						 JwtService jwtService) {
		this.playerService = playerService;
		this.jwtService = jwtService;
	}

	/**
	 * Handles the HTTP POST request to register a new player.
	 *
	 * @param playerRequest The PlayerRequestDto containing player registration information.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/registration")
	public ResponseEntity<InfoResponse> registrationNewPlayer(@RequestBody PlayerRequestDto playerRequest) {
		playerService.registrationPlayer(playerRequest);
		return generateResponse(HttpStatus.OK, "You have successfully registered.");
	}

	/**
	 * Handles the HTTP POST request to log in a player.
	 *
	 * @param playerRequest The PlayerRequestDto containing player login information.
	 * @param resp          The HttpServletResponse object.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	@PostMapping("/log_in")
	public ResponseEntity<InfoResponse> logIn(@RequestBody PlayerRequestDto playerRequest,
											  HttpServletResponse resp) {
		AuthPlayer authPlayer = playerService.logIn(playerRequest);
		addedHeader(authPlayer, resp);
		return generateResponse(HttpStatus.OK, "You've successfully logged in");
	}

	/**
	 * Handles the HTTP GET request to retrieve the player balance.
	 *
	 * @param request The HttpServletRequest object.
	 * @return ResponseEntity containing the BalanceResponseDto and HTTP status.
	 */
	@GetMapping("/balance")
	public ResponseEntity<BalanceResponseDto> getBalance(HttpServletRequest request) {
		AuthPlayer authPlayer = (AuthPlayer) request.getAttribute("authPlayer");
		BalanceResponseDto balanceResponse = playerService.getPlayerBalance(authPlayer);
		return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
	}

	/**
	 * Adds JWT token to the response header with player role and ID as extra claims.
	 *
	 * @param authPlayer The AuthPlayer containing player information.
	 * @param resp       The HttpServletResponse object.
	 */
	private void addedHeader(AuthPlayer authPlayer, HttpServletResponse resp) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", authPlayer.role().name());
		extraClaims.put("playerID", authPlayer.playerID());
		String jwtToken = jwtService.generateWebToken(extraClaims, authPlayer);
		resp.addHeader("Authorization", "Bearer " + jwtToken);
	}

	/**
	 * Generates a ResponseEntity with InfoResponse and the provided HTTP status and message.
	 *
	 * @param status  The HTTP status.
	 * @param message The response message.
	 * @return ResponseEntity containing the InfoResponse and HTTP status.
	 */
	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}
}