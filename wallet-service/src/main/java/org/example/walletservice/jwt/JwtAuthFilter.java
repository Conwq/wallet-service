package org.example.walletservice.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.walletservice.model.dto.AuthPlayer;
import org.example.walletservice.model.enums.Role;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * Extracts and validates JWT (JSON Web Token) from the Authorization header of an HTTP request.
 */
@Component
@RequiredArgsConstructor
public class JwtAuthFilter extends OncePerRequestFilter {
	private final JwtService jwtService;

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
				Role role = Role.valueOf(roleParam);
				AuthPlayer authPlayer = new AuthPlayer(playerID, usernameParam, role);
				request.setAttribute("authPlayer", authPlayer);
			}
		}
		filterChain.doFilter(request, response);
	}
}