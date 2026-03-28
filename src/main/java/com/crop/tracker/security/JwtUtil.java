package com.crop.tracker.security;

import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtUtil {

    @Value("${jwt.secret:crop_secret_key_for_jwt_tokens_2024}")
    private String secret;

    @Value("${jwt.expiration:86400000}")
    private Long expiration;

    // Generate token
    public String generateToken(String mobile) {
        return Jwts.builder()
                .subject(mobile)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSigningKey())
                .compact();
    }

    // Get signing key
    private SecretKey getSigningKey() {
        return Keys.hmacShaKeyFor(secret.getBytes());
    }

    // Extract mobile from token
    public String extractMobile(String token) {
        return getClaims(token).getSubject();
    }

    // Extract expiration
    public Date extractExpiration(String token) {
        return getClaims(token).getExpiration();
    }

    // Get all claims
    private Claims getClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    // Check if token is expired
    private boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }

    // Validate token
    public boolean validateToken(String token, String mobile) {
        try {
            String extractedMobile = extractMobile(token);
            return (extractedMobile.equals(mobile) && !isTokenExpired(token));
        } catch (Exception e) {
            return false;
        }
    }

    // Simple validate
    public boolean validateToken(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            return false;
        }
    }
}