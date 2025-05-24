//package com.nexbus.nexbus_backend.config;
//
//import io.jsonwebtoken.Claims;
//import io.jsonwebtoken.Jwts;
//import io.jsonwebtoken.SignatureAlgorithm;
//import io.jsonwebtoken.security.Keys;
//import org.springframework.stereotype.Component;
//
//import java.security.Key;
//import java.util.Date;
//import java.nio.charset.StandardCharsets;
//
//@Component
//public class JwtUtil {
//    // Extended to 64+ characters to ensure >= 512 bits (64 bytes)
//    private final String SECRET_KEY = "nexbus-secret-key-2025-nexbus-secret-key-2025-extended-to-meet-hs512-requirements-for-security";
//    private final long EXPIRATION_TIME = 1000 * 60 * 60; // 1 hour
//    private final Key key;
//
//    public JwtUtil() {
//        this.key = Keys.hmacShaKeyFor(SECRET_KEY.getBytes(StandardCharsets.UTF_8));
//    }
//
//    public String generateToken(String username, String role) {
//        return Jwts.builder()
//                .setSubject(username)
//                .claim("role", role)
//                .setIssuedAt(new Date())
//                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
//                .signWith(key, SignatureAlgorithm.HS512)
//                .compact();
//    }
//
//    public Claims extractClaims(String token) {
//        return Jwts.parser()
//                .setSigningKey(key)
//                .parseClaimsJws(token)
//                .getBody();
//    }
//
//    public String extractUsername(String token) {
//        return extractClaims(token).getSubject();
//    }
//
//    public String extractRole(String token) {
//        return extractClaims(token).get("role", String.class);
//    }
//
//    public boolean isTokenValid(String token, String username) {
//        return (extractUsername(token).equals(username) && !isTokenExpired(token));
//    }
//
//    private boolean isTokenExpired(String token) {
//        return extractClaims(token).getExpiration().before(new Date());
//    }
//}