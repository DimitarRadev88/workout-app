package com.dimitarrradev.userService.app.security;

import com.dimitarrradev.userService.app.role.Role;
import com.dimitarrradev.userService.app.role.enums.RoleType;
import com.dimitarrradev.userService.app.user.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseCookie;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtServiceTests {

    private static final String SECRET = "test-secret-for-signing-jwt-tokens-in-user-service-tests-only-at-least-64-bytes-long";

    private final JwtService jwtService = new JwtService(SECRET, 3600, false);

    @Test
    void testIssueTokenContainsUserIdUsernameRolesAndExpiration() {
        User user = user(7L, "gym-rat", RoleType.USER, RoleType.ADMIN);

        Claims claims = parse(jwtService.issueToken(user), SECRET);

        assertEquals("7", claims.getSubject());
        assertEquals("gym-rat", claims.get("username", String.class));
        assertEquals(List.of("USER", "ADMIN"), claims.get("roles", List.class));
        assertEquals(Duration.ofHours(1).toSeconds(),
                (claims.getExpiration().getTime() - claims.getIssuedAt().getTime()) / 1000);
    }

    @Test
    void testTokenSignedWithAnotherSecretIsRejected() {
        String token = jwtService.issueToken(user(7L, "gym-rat", RoleType.USER));

        assertThrows(SignatureException.class,
                () -> parse(token, "another-secret-that-is-also-long-enough-for-hs512-signing-but-different"));
    }

    @Test
    void testCreateCookieIsHttpOnlyLaxAndLivesAsLongAsTheToken() {
        ResponseCookie cookie = jwtService.createCookie("signed-token");

        assertEquals(JwtService.COOKIE_NAME, cookie.getName());
        assertEquals("signed-token", cookie.getValue());
        assertTrue(cookie.isHttpOnly());
        assertFalse(cookie.isSecure());
        assertEquals("Lax", cookie.getSameSite());
        assertEquals("/", cookie.getPath());
        assertEquals(Duration.ofHours(1), cookie.getMaxAge());
    }

    @Test
    void testSecretShorterThan64BytesIsRejectedAtStartup() {
        assertThrows(IllegalStateException.class, () -> new JwtService("too-short", 3600, false));
    }

    private static Claims parse(String token, String secret) {
        return Jwts.parser()
                .verifyWith(Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseSignedClaims(token)
                .getPayload();
    }

    private static User user(Long id, String username, RoleType... roleTypes) {
        List<Role> roles = new ArrayList<>();
        for (RoleType roleType : roleTypes) {
            Role role = new Role();
            role.setRoleType(roleType);
            roles.add(role);
        }

        User user = new User();
        user.setId(id);
        user.setUsername(username);
        user.setRoles(roles);
        return user;
    }

}
