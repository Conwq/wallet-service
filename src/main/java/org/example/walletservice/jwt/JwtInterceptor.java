package org.example.walletservice.jwt;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.NonNull;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * Extracts and validates JWT (JSON Web Token) from the Authorization header of an HTTP request.
 */
public class JwtInterceptor implements HandlerInterceptor {

	private final JwtService jwtService;

	@Autowired
	public JwtInterceptor(JwtService jwtService) {
		this.jwtService = jwtService;
	}

	/**
	 * Pre-handle method that extracts and validates JWT from the Authorization header of the HTTP request.
	 *
	 * @param req     The HttpServletRequest object.
	 * @param res     The HttpServletResponse object.
	 * @param handler The handler object.
	 * @return True if the request is allowed to proceed, false otherwise.
	 */
	@Override
	public boolean preHandle(@NonNull HttpServletRequest req,
							 @NonNull HttpServletResponse res,
							 @NonNull Object handler) {
		final String authHeader = req.getHeader("Authorization");

		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			String jwt = authHeader.substring(7);

			int playerID = jwtService.extractPlayerID(jwt);
			String username = jwtService.extractUsername(jwt);
			String roleParam = jwtService.extractRole(jwt);

			if (username != null && roleParam != null && playerID > -1) {
				Role role = Role.valueOf(roleParam);
				AuthPlayerDto authPlayerDto = new AuthPlayerDto(playerID, username, role);
				req.setAttribute("authPlayer", authPlayerDto);
			}
		}
		return true;
	}
}
