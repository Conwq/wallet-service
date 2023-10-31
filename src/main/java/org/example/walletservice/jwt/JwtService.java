package org.example.walletservice.jwt;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.example.walletservice.model.dto.AuthPlayerDto;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.function.Function;

/**
 * A service class for handling JSON Web Token (JWT) operations, including token generation and extraction of claims.
 */
public class JwtService {
	private static final String SECRET_KEY = "f9c9f2c74e849e48aac676442f8eaa67fbc13f62999f8d30ae9417518bd38456";

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
	 * Extracts the player ID claim from the provided JWT.
	 *
	 * @param jwt The JWT from which to extract the player ID claim.
	 * @return The player ID extracted from the JWT.
	 */
	public int extractPlayerID(String jwt) {
		return extractClaim(jwt, claims -> claims.get("playerID", Integer.class));
	}

	/**
	 * Extracts the role claim from the provided JWT.
	 *
	 * @param jwt The JWT from which to extract the role claim.
	 * @return The role extracted from the JWT.
	 */
	public String extractRole(String jwt) {
		return extractClaim(jwt, claims -> claims.get("role", String.class));
	}

	/**
	 * Generates a JWT with the specified extra claims and player details.
	 *
	 * @param extraClaims   Additional claims to include in the JWT.
	 * @param authPlayerDto The player details used to generate the JWT.
	 * @return The generated JWT.
	 */
	public String generateWebToken(Map<String, Object> extraClaims, AuthPlayerDto authPlayerDto) {
		return Jwts.builder()
				.setClaims(extraClaims)
				.setSubject(authPlayerDto.username())
				.setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + 12000000))
				.signWith(generateKey(), SignatureAlgorithm.HS256)
				.compact();
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
		byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
		return Keys.hmacShaKeyFor(keyBytes);
	}
}
