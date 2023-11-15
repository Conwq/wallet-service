package org.example.walletservice.config;

import lombok.RequiredArgsConstructor;
import org.example.walletservice.repository.PlayerRepository;
import org.example.walletservice.service.exception.PlayerNotFoundException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * Configuration class for application-related beans and settings.
 * This class defines and configures beans such as UserDetailsService, PasswordEncoder, and AuthenticationProvider.
 */
@Configuration
@RequiredArgsConstructor
public class ApplicationConfig {
	public final PlayerRepository playerRepository;

	/**
	 * Creates and configures a UserDetailsService bean.
	 *
	 * @return The configured UserDetailsService bean.
	 */
	@Bean
	public UserDetailsService userDetailsService() {
		return username -> playerRepository.findByUsername(username)
				.orElseThrow(() -> new PlayerNotFoundException("Player not found."));
	}

	/**
	 * Creates and configures a BCryptPasswordEncoder bean for password encoding.
	 *
	 * @return The configured PasswordEncoder bean.
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	/**
	 * Creates and configures an AuthenticationProvider bean using DaoAuthenticationProvider.
	 *
	 * @return The configured AuthenticationProvider bean.
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
		authenticationProvider.setUserDetailsService(userDetailsService());
		authenticationProvider.setPasswordEncoder(passwordEncoder());
		return authenticationProvider;
	}

	/**
	 * Creates and configures an AuthenticationManager bean using the provided AuthenticationConfiguration.
	 *
	 * @param configuration The AuthenticationConfiguration used to obtain the AuthenticationManager.
	 * @return The configured AuthenticationManager bean.
	 * @throws Exception If an error occurs during configuration.
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
		return configuration.getAuthenticationManager();
	}
}
