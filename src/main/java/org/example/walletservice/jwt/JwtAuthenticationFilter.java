package org.example.walletservice.jwt;

import jakarta.servlet.DispatcherType;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.walletservice.context.ApplicationContextHolder;
import org.example.walletservice.model.Role;
import org.example.walletservice.model.dto.AuthPlayerDto;

import java.io.IOException;

/**
 * This filter intercepts incoming HTTP requests to extract and validate JWT (JSON Web Token) authentication.
 * If a valid token is found, it extracts the player details and sets them in the request attribute for further processing.
 * This filter is configured to intercept requests for all URL patterns in both REQUEST and FORWARD dispatcher types.
 */
@WebFilter(urlPatterns = "/*", dispatcherTypes = {DispatcherType.REQUEST, DispatcherType.FORWARD})
public class JwtAuthenticationFilter extends HttpFilter {

	/**
	 * Handles the filtering of incoming HTTP requests.
	 *
	 * @param req   The HttpServletRequest object representing the incoming request.
	 * @param res   The HttpServletResponse object representing the outgoing response.
	 * @param chain The FilterChain object for invoking the next filter in the chain.
	 * @throws IOException      If an I/O error occurs.
	 * @throws ServletException If a servlet-specific error occurs.
	 */
	@Override
	protected void doFilter(HttpServletRequest req,
							HttpServletResponse res,
							FilterChain chain) throws IOException, ServletException {
		ApplicationContextHolder contextHolder = ApplicationContextHolder.getInstance();
		JwtService jwtService = contextHolder.getJwtService();

		final String authHeader = req.getHeader("Authorization");
		final String jwt;
		final String username;
		final int playerID;
		final String roleParam;

		if (authHeader != null) {
			jwt = authHeader.substring(7);
			playerID = jwtService.extractPlayerID(jwt);
			username = jwtService.extractUsername(jwt);
			roleParam = jwtService.extractRole(jwt);

			if (username != null && roleParam != null && playerID > -1) {
				Role role = Role.valueOf(roleParam);
				AuthPlayerDto authPlayerDto = new AuthPlayerDto(playerID, username, role);
				req.setAttribute("authPlayer", authPlayerDto);
			}
		}
		chain.doFilter(req, res);
	}
}
