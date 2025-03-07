package com.argentbank.argentbankApi.serviceTest.jwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import java.util.Date;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;

import com.argentbank.argentbankApi.exception.HttpWithMsgException;
import com.argentbank.argentbankApi.service.JwtBlacklistService;
import com.argentbank.argentbankApi.service.JwtService;

import io.jsonwebtoken.Jwts;

@SpringBootTest
public class getUserFromTokenTest {

    @Mock
    private JwtBlacklistService jwtBlacklistService;

    private JwtService jwtService;

    @BeforeEach
    void setUp() {
        String secret = "fh7ZBsC7I9OokW92A1dMXjx6lufpMNKO6gU2sXXtNr0";
        jwtService = new JwtService(jwtBlacklistService, secret);
    }

    @Test
    void getUserFromValidToken() throws Exception {
        String userEmail = "test@gmail.com";
        String token = jwtService.generateToken(userEmail);
        String BearerToken = "Bearer " + token;

        when(jwtBlacklistService.isBlackListed(BearerToken)).thenReturn(false);

        // act
        String userEmailFromToken = jwtService.getUserFromToken(BearerToken);

        assertEquals(userEmail, userEmailFromToken, "User extract from token must be correct");
    }

    @Test
    void getUserFromExpiredTokenShouldFail() throws Exception {
        String userEmail = "test@gmail.com";
        String expiredToken = Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date(System.currentTimeMillis() - 2 * 60 * 60 * 1000L)) // create two hours ago
                .expiration(new Date(System.currentTimeMillis() - 60 * 60 * 1000L)) // expired one hours ago
                .signWith(jwtService.getSecretKey())
                .compact();
        String bearerToken = "Bearer " + expiredToken;

        when(jwtBlacklistService.isBlackListed(expiredToken)).thenReturn(false);

        // act
        HttpWithMsgException exception = assertThrows(HttpWithMsgException.class,
                () -> jwtService.getUserFromToken(bearerToken));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Token expired", exception.getMessage());
    }

    @Test
    void getUserFromInvalidTokenShouldFail() throws Exception {
        String malformedToken = "invalid.token";

        // act
        HttpWithMsgException exception = assertThrows(HttpWithMsgException.class,
                () -> jwtService.getUserFromToken(malformedToken));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Invalid token format, does not have 'Bearer '", exception.getMessage());
    }

    @Test
    void getUserFromBlackListedTokenShouldFail() throws Exception {
        String userBlackListed = "blacklistedUser@gmail.com";
        String token = jwtService.generateToken(userBlackListed);
        String bearerToken = "Bearer " + token;

        when(jwtBlacklistService.isBlackListed(token)).thenReturn(true);

        // act
        HttpWithMsgException exception = assertThrows(HttpWithMsgException.class,
                () -> jwtService.getUserFromToken(bearerToken));
        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Token is blacklisted", exception.getMessage());
    }
}
