package org.example.walletservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * A service class for handling JSON Web Token (JWT) operations, including token generation and extraction of claims.
 */
@Service
public class JwtService {
	@Value("${spring.jwt.secret-key}")
	private String secretKey;

	/**
	 * Extracts the username claim from the provided JWT.
	 *
	 * @param jwt The JWT from which to extract the username claim.
	 * @return The username extracted from the JWT.
	 */
	public String extractUsername(String jwt) {
		return extractClaim(jwt, Claims::getSubject);
	}

	/**
	 * Generates a JWT with the specified extra claims and player details.
	 *
	 * @param extraClaims   Additional claims to include in the JWT.
	 * @param userDetails The player details used to generate the JWT.
	 * @return The generated JWT.
	 */
	public String generateWebToken(Map<String, Object> extraClaims, UserDetails userDetails) {
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(userDetails.getUsername())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 12000000))
				.signWith(generateKey(), SignatureAlgorithm.HS256)
				.compact();
	}

	/**
	 * Generates a JWT with the specified extra claims and player details.
	 *
	 * @param userDetails The player details used to generate the JWT.
	 * @return The generated JWT.
	 */
	public String generateWebToken(UserDetails userDetails) {
		return generateWebToken(new HashMap<>(), userDetails);
	}

	public boolean isTokenValid(String jwt, UserDetails userDetails) {
		final String username = extractUsername(jwt);
		return username.equals(userDetails.getUsername()) && !isTokenExpired(jwt);
	}

	private boolean isTokenExpired(String jwt) {
		return extractExpiration(jwt).before(new Date());
	}

	private Date extractExpiration(String jwt) {
		return extractClaim(jwt, Claims::getExpiration);
	}

	/**
	 * Extracts a specific claim from the provided JWT using the given claims resolver.
	 *
	 * @param jwt            The JWT from which to extract the claim.
	 * @param claimsResolver The function to resolve the desired claim from the JWT claims.
	 * @param <T>            The type of the extracted claim.
	 * @return The extracted claim.
	 */
	public <T> T extractClaim(String jwt, Function<Claims, T> claimsResolver) {
		final Claims claims = extractAllClaims(jwt);
		return claimsResolver.apply(claims);
	}

	/**
	 * Extracts all claims from the provided JWT.
	 *
	 * @param jwt The JWT from which to extract all claims.
	 * @return The claims extracted from the JWT.
	 */
	private Claims extractAllClaims(String jwt) {
		return Jwts.parser()
				.setSigningKey(generateKey())
				.build()
				.parseClaimsJws(jwt)
				.getBody();
	}

	/**
	 * Generates a cryptographic key for JWT signing and verification based on the secret key.
	 *
	 * @return The generated cryptographic key.
	 */
	private Key generateKey() {
		byte[] keyBytes = Decoders.BASE64.decode(secretKey);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
