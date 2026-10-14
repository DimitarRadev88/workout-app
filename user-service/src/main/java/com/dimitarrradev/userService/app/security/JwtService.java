package com.dimitarrradev.userService.app.security;

import com.dimitarrradev.userService.app.user.User;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

@Service
public class JwtService {

    public static final String COOKIE_NAME = "JWT";

    private static final int MIN_SECRET_BYTES = 64;

    private final SecretKey secretKey;
    private final Duration expirationTime;
    private final boolean secureCookie;

    public JwtService(
            @Value("${token.secret}") String secret,
            @Value("${token.expiration-time}") long expirationSeconds,
            @Value("${token.cookie-secure}") boolean secureCookie
    ) {
        byte[] secretBytes = secret.getBytes(StandardCharsets.UTF_8);
        if (secretBytes.length < MIN_SECRET_BYTES) {
            throw new IllegalStateException("token.secret must be at least " + MIN_SECRET_BYTES + " bytes long for HS512");
        }

        this.secretKey = Keys.hmacShaKeyFor(secretBytes);
        this.expirationTime = Duration.ofSeconds(expirationSeconds);
        this.secureCookie = secureCookie;
    }

    public String issueToken(User user) {
        Instant now = Instant.now();

        return Jwts.builder()
                .subject(user.getId().toString())
                .claim("username", user.getUsername())
                .claim("roles", user.getRoles().stream()
                        .map(role -> role.getRoleType().name())
                        .toList())
                .issuedAt(Date.from(now))
                .expiration(Date.from(now.plus(expirationTime)))
                .signWith(secretKey, Jwts.SIG.HS512)
                .compact();
    }

    public ResponseCookie createCookie(String token) {
        return ResponseCookie.from(COOKIE_NAME, token)
                .httpOnly(true)
                .secure(secureCookie)
                .sameSite("Lax")
                .path("/")
                .maxAge(expirationTime)
                .build();
    }

}
