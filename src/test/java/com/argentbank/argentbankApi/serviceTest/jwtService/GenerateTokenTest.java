package com.argentbank.argentbankApi.serviceTest.jwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.service.JwtService;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

@SpringBootTest
public class GenerateTokenTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void generateTokenWithSuccess() throws Exception {
        String user = "testUser@gmail.com";

        // act
        String token = jwtService.generateToken(user);

        assertNotNull(token, "Token must not be null");
        assertFalse(token.isEmpty(), "Toen must not be empty");

        Claims claims = Jwts.parser()
                .verifyWith(jwtService.getSecretKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

        assertEquals(user, claims.getSubject(), "Subject of the token must match the user");

        long expiration = claims.getExpiration().getTime();
        long expectedExpiration = claims.getIssuedAt().getTime() + (60 * 60 * 1000);

        assertEquals(expectedExpiration, expiration, "Expiration must be an hour after the creation of the token");
    }
}
