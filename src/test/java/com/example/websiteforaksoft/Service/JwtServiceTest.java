package com.example.websiteforaksoft.Service;

import com.example.websiteforaksoft.Entity.User;
import com.example.websiteforaksoft.Enum.Role;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JwtService Tests")
class JwtServiceTest {

    private JwtService jwtService;
    private User testUser;
    private String testSecret = "test-secret-key-for-jwt-tokens-that-is-at-least-256-bits-long-to-satisfy-HS256-requirements-and-security";
    private Long testExpiration = 7200000L; // 1 hour

    @BeforeEach
    void setUp() {
        jwtService = new JwtService();

        ReflectionTestUtils.setField(jwtService, "secret", testSecret);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        testUser = new User();
        testUser.setId(1L);
        testUser.setEmail("test@gmail.com");
        testUser.setName("Test User");
        testUser.setRole(Role.ROLE_USER);
        testUser.setPassword("password");
        testUser.setIsActive(true);
    }

    @Test
    @DisplayName("Should generate valid JWT token")
    void generateToken_Success() {
        String token = jwtService.generateToken(testUser);

        assertNotNull(token);
        assertFalse(token.isEmpty());

        String[] parts = token.split("\\.");
        assertEquals(3, parts.length, "JWT should have 3 parts separated by dots");

        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("Should include username (email) in token subject")
    void generateToken_ContainsUsername() {
        String token = jwtService.generateToken(testUser);
        String extractedUsername = jwtService.extractUsername(token);

        assertEquals("test@gmail.com", extractedUsername);
    }

    @Test
    @DisplayName("Should include role in token claims")
    void generateToken_ContainsRole() {
        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("ROLE_USER", claims.get("role", String.class));
    }

    @Test
    @DisplayName("Should include correct role for ADMIN user")
    void generateToken_AdminRole() {
        testUser.setRole(Role.ROLE_ADMIN);

        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals("ROLE_ADMIN", claims.get("role", String.class));
    }

    @Test
    @DisplayName("Should set issuedAt within reasonable time range")
    void generateToken_SetsIssuedAt() {
        long beforeGeneration = System.currentTimeMillis() - 100; // 100ms tolerance

        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        long afterGeneration = System.currentTimeMillis() + 100; // 100ms tolerance

        Date issuedAt = claims.getIssuedAt();
        assertNotNull(issuedAt);
        assertTrue(issuedAt.getTime() >= beforeGeneration,
                "IssuedAt should be after or at generation start");
        assertTrue(issuedAt.getTime() <= afterGeneration,
                "IssuedAt should be before or at generation end");
    }

    @Test
    @DisplayName("Should set expiration time correctly")
    void generateToken_SetsExpiration() {
        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();

        assertNotNull(expiration);
        assertNotNull(issuedAt);

        long difference = expiration.getTime() - issuedAt.getTime();
        assertEquals(testExpiration, difference, 1000); // 1 second tolerance
    }

    @Test
    @DisplayName("Should generate different tokens for different users")
    void generateToken_DifferentUsersProduceDifferentTokens() {
        User anotherUser = new User();
        anotherUser.setEmail("another@gmail.com");
        anotherUser.setRole(Role.ROLE_USER);
        anotherUser.setPassword("password");
        anotherUser.setIsActive(true);

        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(anotherUser);

        assertNotEquals(token1, token2);
    }

    @Test
    @DisplayName("Should extract username from valid token")
    void extractUsername_Success() {
        String token = jwtService.generateToken(testUser);

        String username = jwtService.extractUsername(token);

        assertEquals("test@gmail.com", username);
    }

    @Test
    @DisplayName("Should throw exception for malformed token")
    void extractUsername_MalformedToken() {
        String malformedToken = "this.is.not.a.valid.token";

        assertThrows(MalformedJwtException.class, () -> {
            jwtService.extractUsername(malformedToken);
        });
    }

    @Test
    @DisplayName("Should throw exception for token with invalid signature")
    void extractUsername_InvalidSignature() {
        String differentSecret = "different-secret-key-that-is-also-at-least-256-bits-long-for-HS256-algorithm-requirements";
        SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String tokenWithWrongSignature = Jwts.builder()
                .subject(testUser.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + testExpiration))
                .signWith(differentKey)
                .compact();

        assertThrows(SignatureException.class, () -> {
            jwtService.extractUsername(tokenWithWrongSignature);
        });
    }

    @Test
    @DisplayName("Should throw exception for null token")
    void extractUsername_NullToken() {
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername(null);
        });
    }

    @Test
    @DisplayName("Should throw exception for empty token")
    void extractUsername_EmptyToken() {
        assertThrows(Exception.class, () -> {
            jwtService.extractUsername("");
        });
    }

    @Test
    @DisplayName("Should validate correct token for correct user")
    void isTokenValid_ValidToken_CorrectUser() {
        String token = jwtService.generateToken(testUser);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should reject token for wrong user")
    void isTokenValid_ValidToken_WrongUser() {
        String token = jwtService.generateToken(testUser);

        User differentUser = new User();
        differentUser.setEmail("different@gmail.com");
        differentUser.setRole(Role.ROLE_USER);
        differentUser.setPassword("password");
        differentUser.setIsActive(true);

        boolean isValid = jwtService.isTokenValid(token, differentUser);

        assertFalse(isValid);
    }

    @Test
    @DisplayName("Should reject expired token")
    void isTokenValid_ExpiredToken() {
        ReflectionTestUtils.setField(jwtService, "expiration", -10000L); // Expired 10 seconds ago
        String expiredToken = jwtService.generateToken(testUser);

        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(expiredToken, testUser);
        });
    }

    @Test
    @DisplayName("Should reject malformed token")
    void isTokenValid_MalformedToken() {
        String malformedToken = "malformed.token.here";

        assertThrows(Exception.class, () -> {
            jwtService.isTokenValid(malformedToken, testUser);
        });
    }

    @Test
    @DisplayName("Should reject token with invalid signature")
    void isTokenValid_InvalidSignature() {
        String differentSecret = "completely-different-secret-key-minimum-256-bits-for-security-requirements-hs256";
        SecretKey differentKey = Keys.hmacShaKeyFor(differentSecret.getBytes(StandardCharsets.UTF_8));

        String tokenWithInvalidSignature = Jwts.builder()
                .subject(testUser.getEmail())
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + testExpiration))
                .signWith(differentKey)
                .compact();

        assertThrows(SignatureException.class, () -> {
            jwtService.isTokenValid(tokenWithInvalidSignature, testUser);
        });
    }

    @Test
    @DisplayName("Should accept token that is about to expire but not yet expired")
    void isTokenValid_TokenAboutToExpire() {
        ReflectionTestUtils.setField(jwtService, "expiration", 5000L);
        String token = jwtService.generateToken(testUser);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        boolean isValid = jwtService.isTokenValid(token, testUser);

        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should throw ExpiredJwtException when parsing expired token")
    void expiredToken_ThrowsException() {
        ReflectionTestUtils.setField(jwtService, "expiration", -10000L);
        String expiredToken = jwtService.generateToken(testUser);
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.extractUsername(expiredToken);
        });
    }

    @Test
    @DisplayName("Full token lifecycle: generate, extract, validate")
    void fullTokenLifecycle_Success() {
        String token = jwtService.generateToken(testUser);
        assertNotNull(token);

        String extractedUsername = jwtService.extractUsername(token);
        assertEquals(testUser.getEmail(), extractedUsername);

        boolean isValid = jwtService.isTokenValid(token, testUser);
        assertTrue(isValid);
    }

    @Test
    @DisplayName("Should handle multiple tokens for same user")
    void multipleTokens_SameUser() throws InterruptedException {
        String token1 = jwtService.generateToken(testUser);

        Thread.sleep(1100);

        String token2 = jwtService.generateToken(testUser);

        assertNotEquals(token1, token2, "Tokens generated at different times should differ");
        assertTrue(jwtService.isTokenValid(token1, testUser));
        assertTrue(jwtService.isTokenValid(token2, testUser));

        String username1 = jwtService.extractUsername(token1);
        String username2 = jwtService.extractUsername(token2);
        assertEquals(username1, username2, "Both tokens should have same username");
    }

    @Test
    @DisplayName("Should extract correct role from token")
    void extractRole_FromToken() {
        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        String role = claims.get("role", String.class);

        assertEquals("ROLE_USER", role);
    }

    @Test
    @DisplayName("Should handle token with special characters in email")
    void generateToken_SpecialCharactersInEmail() {
        testUser.setEmail("test+tag@example.co.uk");

        String token = jwtService.generateToken(testUser);
        String extractedEmail = jwtService.extractUsername(token);

        assertEquals("test+tag@example.co.uk", extractedEmail);
        assertTrue(jwtService.isTokenValid(token, testUser));
    }

    @Test
    @DisplayName("Token should remain valid throughout its lifetime and expire after")
    void tokenValidation_ThroughoutLifetime() throws InterruptedException {
        ReflectionTestUtils.setField(jwtService, "expiration", 3000L);
        String token = jwtService.generateToken(testUser);

        assertTrue(jwtService.isTokenValid(token, testUser), "Token should be valid immediately");

        Thread.sleep(1500);
        assertTrue(jwtService.isTokenValid(token, testUser), "Token should still be valid at 50% lifetime");

        Thread.sleep(2000);

        assertThrows(ExpiredJwtException.class, () -> {
            jwtService.isTokenValid(token, testUser);
        }, "Token should be expired after lifetime");

        // Restore
        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should handle very long expiration time")
    void generateToken_LongExpiration() {
        Long veryLongExpiration = 31536000000L; // 1 year in milliseconds
        ReflectionTestUtils.setField(jwtService, "expiration", veryLongExpiration);

        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));
        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        Date expiration = claims.getExpiration();
        Date issuedAt = claims.getIssuedAt();
        long actualExpiration = expiration.getTime() - issuedAt.getTime();

        assertEquals(veryLongExpiration, actualExpiration, 1000);
        assertTrue(jwtService.isTokenValid(token, testUser));

        ReflectionTestUtils.setField(jwtService, "expiration", testExpiration);
    }

    @Test
    @DisplayName("Should correctly parse all token claims")
    void parseTokenClaims_Success() {
        String token = jwtService.generateToken(testUser);

        SecretKey key = Keys.hmacShaKeyFor(testSecret.getBytes(StandardCharsets.UTF_8));

        Claims claims = Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertAll("Token claims",
                () -> assertEquals("test@gmail.com", claims.getSubject(), "Subject should be email"),
                () -> assertEquals("ROLE_USER", claims.get("role", String.class), "Role should be present"),
                () -> assertNotNull(claims.getIssuedAt(), "IssuedAt should be present"),
                () -> assertNotNull(claims.getExpiration(), "Expiration should be present")
        );
    }

    @Test
    @DisplayName("Should generate tokens with consistent structure")
    void generateToken_ConsistentStructure() {
        String token1 = jwtService.generateToken(testUser);
        String token2 = jwtService.generateToken(testUser);

        assertEquals(3, token1.split("\\.").length);
        assertEquals(3, token2.split("\\.").length);

        assertTrue(jwtService.isTokenValid(token1, testUser));
        assertTrue(jwtService.isTokenValid(token2, testUser));
    }
}