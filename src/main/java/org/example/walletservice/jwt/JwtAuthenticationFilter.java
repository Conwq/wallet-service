package org.example.walletservice.jwt;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class JwtAuthenticationFilter implements Filter {
	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		System.out.println("1");
	}

	@Override
	public void destroy() {
		System.out.println("2");
	}

	@Override
	public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
		System.out.println("WWW");
	}


	//	private final JwtService jwtService;
//
//	@Autowired
//	public JwtAuthenticationFilter(JwtService jwtService) {
//		this.jwtService = jwtService;
//	}

//	@Override
//	protected void doFilterInternal(@NonNull HttpServletRequest req,
//									@NonNull HttpServletResponse res,
//									@NonNull FilterChain chain) throws ServletException, IOException {
//		System.out.println("Wotk!");
//		chain.doFilter(req, res);
//		final String authHeader = req.getHeader("Authorization");
//		final String jwt;
//		final String username;
//		final int playerID;
//		final String roleParam;
//
//		if (authHeader != null) {
//			jwt = authHeader.substring(7);
//			playerID = jwtService.extractPlayerID(jwt);
//			username = jwtService.extractUsername(jwt);
//			roleParam = jwtService.extractRole(jwt);
//
//			if (username != null && roleParam != null && playerID > -1) {
//				Role role = Role.valueOf(roleParam);
//				AuthPlayerDto authPlayerDto = new AuthPlayerDto(playerID, username, role);
//				req.setAttribute("authPlayer", authPlayerDto);
//			}
//		}
//		System.out.println("Filter");
//		chain.doFilter(req, res);
//	}
}
