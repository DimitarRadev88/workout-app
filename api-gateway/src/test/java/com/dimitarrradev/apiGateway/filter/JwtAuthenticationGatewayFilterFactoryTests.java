package com.dimitarrradev.apiGateway.filter;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.Test;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.http.HttpCookie;
import org.springframework.http.HttpStatus;
import org.springframework.mock.http.server.reactive.MockServerHttpRequest;
import org.springframework.mock.web.server.MockServerWebExchange;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.concurrent.atomic.AtomicBoolean;

import static org.junit.jupiter.api.Assertions.*;

class JwtAuthenticationGatewayFilterFactoryTests {

    private static final String SECRET = "test-secret-for-signing-jwt-tokens-in-gateway-tests-only-at-least-64-bytes-long";

    private final GatewayFilter filter = new JwtAuthenticationGatewayFilterFactory(SECRET)
            .apply(new JwtAuthenticationGatewayFilterFactory.Config());

    @Test
    void testRequestWithoutCookieIsRejected() {
        MockServerWebExchange exchange = MockServerWebExchange.from(MockServerHttpRequest.get("/api/exercises"));

        assertFalse(runFilter(exchange));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testRequestWithValidTokenIsForwarded() {
        MockServerWebExchange exchange = exchangeWithCookie(token(SECRET, Instant.now().plusSeconds(3600)));

        assertTrue(runFilter(exchange));
        assertNull(exchange.getResponse().getStatusCode());
    }

    @Test
    void testTokenSignedWithAnotherSecretIsRejected() {
        MockServerWebExchange exchange = exchangeWithCookie(
                token("another-secret-that-is-also-long-enough-for-hs512-signing-but-different", Instant.now().plusSeconds(3600)));

        assertFalse(runFilter(exchange));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testExpiredTokenIsRejected() {
        MockServerWebExchange exchange = exchangeWithCookie(token(SECRET, Instant.now().minusSeconds(60)));

        assertFalse(runFilter(exchange));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    @Test
    void testMalformedTokenIsRejected() {
        MockServerWebExchange exchange = exchangeWithCookie("not-a-jwt");

        assertFalse(runFilter(exchange));
        assertEquals(HttpStatus.UNAUTHORIZED, exchange.getResponse().getStatusCode());
    }

    private boolean runFilter(MockServerWebExchange exchange) {
        AtomicBoolean forwarded = new AtomicBoolean(false);

        filter.filter(exchange, ex -> {
            forwarded.set(true);
            return Mono.empty();
        }).block();

        return forwarded.get();
    }

    private static MockServerWebExchange exchangeWithCookie(String token) {
        return MockServerWebExchange.from(MockServerHttpRequest.get("/api/exercises")
                .cookie(new HttpCookie(JwtAuthenticationGatewayFilterFactory.COOKIE_NAME, token)));
    }

    private static String token(String secret, Instant expiration) {
        return Jwts.builder()
                .subject("1")
                .issuedAt(Date.from(expiration.minusSeconds(3600)))
                .expiration(Date.from(expiration))
                .signWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)), Jwts.SIG.HS512)
                .compact();
    }

}
