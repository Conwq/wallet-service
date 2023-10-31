package org.example.walletservice.in;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.example.walletservice.jwt.JwtService;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.example.walletservice.model.dto.BalanceResponseDto;
import org.example.walletservice.model.dto.InfoResponse;
import org.example.walletservice.model.dto.PlayerRequestDto;
import org.example.walletservice.service.PlayerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class to perform player operations.
 */
@Api(description = "Endpoints for player operations")
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
	@ApiOperation("Registers a new player with the provided information")
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
	@ApiOperation("Logs in a player with the provided login information")
	@PostMapping("/log_in")
	public ResponseEntity<InfoResponse> logIn(@RequestBody PlayerRequestDto playerRequest,
											  HttpServletResponse resp) {
		AuthPlayerDto authPlayerDto = playerService.logIn(playerRequest);
		addedHeader(authPlayerDto, resp);
		return generateResponse(HttpStatus.OK, "You've successfully logged in");
	}

	/**
	 * Handles the HTTP GET request to retrieve the player balance.
	 *
	 * @param request The HttpServletRequest object.
	 * @return ResponseEntity containing the BalanceResponseDto and HTTP status.
	 */
	@ApiOperation("Retrieves the balance of the authenticated player")
	@GetMapping("/balance")
	public ResponseEntity<BalanceResponseDto> getBalance(HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute("authPlayer");
		BalanceResponseDto balanceResponse = playerService.getPlayerBalance(authPlayerDto);
		return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
	}

	/**
	 * Adds JWT token to the response header with player role and ID as extra claims.
	 *
	 * @param authPlayerDto The AuthPlayerDto containing player information.
	 * @param resp          The HttpServletResponse object.
	 */
	private void addedHeader(AuthPlayerDto authPlayerDto, HttpServletResponse resp) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", authPlayerDto.role().name());
		extraClaims.put("playerID", authPlayerDto.playerID());
		String jwtToken = jwtService.generateWebToken(extraClaims, authPlayerDto);
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