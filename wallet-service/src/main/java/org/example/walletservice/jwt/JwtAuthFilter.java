package org.example.walletservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.model.dto.AuthPlayer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.patseev.auditspringbootstarter.logger.model.Roles;

import java.io.IOException;

/**
 * Extracts and validates JWT (JSON Web Token) from the Authorization header of an HTTP request.
 */
@Component
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

	@Autowired
	public JwtAuthFilter(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	@Override
	protected void doFilterInternal(@NonNull HttpServletRequest request,
									@NonNull HttpServletResponse response,
									@NonNull FilterChain filterChain) throws ServletException, IOException {
		final int playerID;
		final String usernameParam;
		final String roleParam;
		final String authHeader = request.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);

			playerID = jwtService.extractPlayerID(jwt);
			usernameParam = jwtService.extractUsername(jwt);
			roleParam = jwtService.extractRole(jwt);

			if (usernameParam != null && roleParam != null && playerID > -1) {
				Roles role = Roles.valueOf(roleParam);
				AuthPlayer authPlayer = new AuthPlayer(playerID, usernameParam, role);
				request.setAttribute("authPlayer", authPlayer);
			}
		}
		filterChain.doFilter(request, response);
	}
}