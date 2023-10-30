package org.example.walletservice.in;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Controller class to perform player operations.
 */
@RestController
@RequestMapping("/players")
public final class PlayerServlet {
	private static final String AUTH_PLAYER = "authPlayer";
	private final PlayerService playerService;
	private final JwtService jwtService;

	@Autowired
	public PlayerServlet(PlayerService playerService,
						 JwtService jwtService) {
		this.playerService = playerService;
		this.jwtService = jwtService;
	}

	@PostMapping("/registration")
	public ResponseEntity<InfoResponse> registrationNewPlayer(@RequestBody PlayerRequestDto playerRequest) {
		playerService.registrationPlayer(playerRequest);

		return generateResponse(HttpStatus.OK, "You have successfully registered.");
	}

	@PostMapping("/log_in")
	public ResponseEntity<InfoResponse> logIn(@RequestBody PlayerRequestDto playerRequest, HttpServletResponse resp) {
		AuthPlayerDto authPlayerDto = playerService.logIn(playerRequest);
		addedHeader(authPlayerDto, resp);

		return generateResponse(HttpStatus.OK, "You've successfully logged in");
	}

	@GetMapping("/balance")
	public ResponseEntity<BalanceResponseDto> getBalance(HttpServletRequest request) {
		AuthPlayerDto authPlayerDto = (AuthPlayerDto) request.getAttribute(AUTH_PLAYER);
		BalanceResponseDto balanceResponse = playerService.getPlayerBalance(authPlayerDto);
		return new ResponseEntity<>(balanceResponse, HttpStatus.OK);
	}

	private void addedHeader(AuthPlayerDto authPlayerDto, HttpServletResponse resp) {
		Map<String, Object> extraClaims = new HashMap<>();
		extraClaims.put("role", authPlayerDto.role().name());
		extraClaims.put("playerID", authPlayerDto.playerID());
		String jwtToken = jwtService.generateWebToken(extraClaims, authPlayerDto);
		resp.addHeader("Authorization", "Bearer " + jwtToken);
	}

	private ResponseEntity<InfoResponse> generateResponse(HttpStatus status, String message) {
		InfoResponse infoResponse = new InfoResponse(new Date().toString(), status.value(), message);
		return new ResponseEntity<>(infoResponse, status);
	}
}