package com.basiccrud.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Date;
import java.util.Optional;

@Service
public class JwtService {

    private final long expirationMs;
    private final SecretKey signingKey;

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs) throws NoSuchAlgorithmException {
        this.expirationMs = expirationMs;
        byte[] digest = MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8));
        this.signingKey = Keys.hmacShaKeyFor(digest);
    }

    public long getExpirationSeconds() {
        return expirationMs / 1000;
    }

    public String generateToken(UserDetails userDetails) {
        Date issuedAt = new Date();
        Date expiresAt = new Date(issuedAt.getTime() + expirationMs);
        return Jwts.builder()
                .subject(userDetails.getUsername())
                .claim(
                        "roles",
                        userDetails.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList())
                .issuedAt(issuedAt)
                .expiration(expiresAt)
                .signWith(signingKey)
                .compact();
    }

    public Optional<String> extractUsername(String token) {
        try {
            Claims claims =
                    Jwts.parser().verifyWith(signingKey).build().parseSignedClaims(token).getPayload();
            return Optional.ofNullable(claims.getSubject());
        } catch (JwtException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
