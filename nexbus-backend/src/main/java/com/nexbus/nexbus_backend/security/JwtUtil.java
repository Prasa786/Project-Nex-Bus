package com.nexbus.nexbus_backend.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Component
public class JwtUtil {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtil.class);

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
    }

    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("role", userDetails.getAuthorities().iterator().next().getAuthority());
        if (userDetails instanceof CustomUserDetails) {
            claims.put("userId", ((CustomUserDetails) userDetails).getUserId());
            logger.debug("Added userId: {} to JWT claims for user: {}", 
                ((CustomUserDetails) userDetails).getUserId(), userDetails.getUsername());
        } else {
            logger.warn("UserDetails is not CustomUserDetails for user: {}", userDetails.getUsername());
        }
        logger.debug("Generating token for user: {}", userDetails.getUsername());
        return Jwts.builder()
            .setClaims(claims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + expiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS512)
            .compact();
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public Integer extractUserId(String token) {
        try {
            Object userIdClaim = extractClaim(token, claims -> claims.get("userId"));
            if (userIdClaim == null) {
                logger.error("No userId claim found in token");
                throw new IllegalArgumentException("Missing userId in token");
            }
            if (userIdClaim instanceof Integer) {
                return (Integer) userIdClaim;
            } else if (userIdClaim instanceof String) {
                try {
                    return Integer.parseInt((String) userIdClaim);
                } catch (NumberFormatException e) {
                    logger.error("userId claim is not a valid integer: {}", userIdClaim);
                    throw new IllegalArgumentException("Invalid userId format: " + userIdClaim);
                }
            } else {
                logger.error("userId claim is of unexpected type: {}", userIdClaim.getClass().getName());
                throw new IllegalArgumentException("Invalid userId type: " + userIdClaim.getClass().getName());
            }
        } catch (Exception e) {
            logger.error("Failed to extract userId from token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
        } catch (Exception e) {
            logger.error("Failed to parse JWT token: {}", e.getMessage());
            throw new RuntimeException("Invalid JWT token: " + e.getMessage(), e);
        }
    }

    public boolean isTokenValid(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        boolean isValid = username.equals(userDetails.getUsername()) && !isTokenExpired(token);
        logger.debug("Token validation for user: {} - valid: {}", username, isValid);
        return isValid;
    }

    private boolean isTokenExpired(String token) {
        boolean isExpired = extractClaim(token, Claims::getExpiration).before(new Date());
        logger.debug("Token expiration check - expired: {}", isExpired);
        return isExpired;
    }

    public String extractRole(String token) {
        return extractClaim(token, claims -> claims.get("role", String.class));
    }
}