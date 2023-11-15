package org.example.walletservice.config;

import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.example.walletservice.jwt.JwtAuthFilter;
import org.example.walletservice.model.enums.Role;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Configuration class for handling security configurations in the application.
 * This class defines security settings, such as authentication providers, authorization rules,
 * and exception handling. It also configures filters for JWT authentication.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {
	private final JwtAuthFilter jwtAuthFilter;
	private final AuthenticationProvider authenticationProvider;

	/**
	 * Configures the security filter chain for the application.
	 *
	 * @param httpSecurity The HTTP security configuration to be customized.
	 * @return The configured security filter chain.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.csrf().disable()
				.authorizeHttpRequests()
				.requestMatchers("/players/registration", "/players/log_in").permitAll()
				.requestMatchers("/log/**").hasAuthority(Role.ADMIN.name())
				.anyRequest().authenticated()
				.and()
				.exceptionHandling()
				.accessDeniedHandler((request, response, accessDeniedException) ->
						response.sendError(HttpServletResponse.SC_FORBIDDEN, "Access Denied"))
				.authenticationEntryPoint((request, response, authException) ->
						response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized"))
				.and()
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authenticationProvider(authenticationProvider)
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);
		return httpSecurity.build();
	}
}