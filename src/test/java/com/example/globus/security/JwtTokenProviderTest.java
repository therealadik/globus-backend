package com.example.globus.security;

import com.example.globus.entity.user.User;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javax.crypto.SecretKey;
import java.lang.reflect.Field;
import java.util.Base64;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class JwtTokenProviderTest {

    private JwtTokenProvider provider;

    @BeforeEach
    void setUp() throws Exception {
        provider = new JwtTokenProvider();

        SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256);
        String secret = Base64.getUrlEncoder()
                .withoutPadding()
                .encodeToString(key.getEncoded());

        Field fSecret = JwtTokenProvider.class.getDeclaredField("jwtSecret");
        fSecret.setAccessible(true);
        fSecret.set(provider, secret);

        Field fExp = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        fExp.setAccessible(true);
        fExp.set(provider, 1000L);
    }

    @Test
    void generateToken_notNull_and_subject() {
        User user = new User();
        user.setUsername("john.doe");

        String token = provider.generateToken(user);
        assertNotNull(token, "Token should not be null");

        String subject = provider.getUsernameFromToken(token);
        assertEquals("john.doe", subject, "Subject must match username");
    }

    @Test
    void validateToken_immediatelyValid() {
        User user = new User();
        user.setUsername("alice");

        String token = provider.generateToken(user);
        assertTrue(provider.validateToken(token), "Token should be valid immediately after generation");
    }

    @Test
    void validateToken_expired() throws Exception {
        Field fExp = JwtTokenProvider.class.getDeclaredField("jwtExpirationInMs");
        fExp.setAccessible(true);
        fExp.set(provider, -1000L);

        User user = new User();
        user.setUsername("expiredUser");

        String token = provider.generateToken(user);
        assertThrows(ExpiredJwtException.class,
                () -> provider.validateToken(token),
                "Expired token must throw ExpiredJwtException");
    }

    @Test
    void getUsernameFromToken_invalid_throws() {
        String badToken = "this.is.not.a.jwt";
        assertThrows(Exception.class, () -> provider.getUsernameFromToken(badToken),
                "Parsing malformed token should throw");
    }
}
