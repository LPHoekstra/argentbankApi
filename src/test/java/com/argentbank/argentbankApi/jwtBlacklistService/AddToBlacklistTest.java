package com.argentbank.argentbankApi.jwtBlacklistService;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Base64;
import java.util.Date;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.service.JwtBlacklistService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
public class AddToBlacklistTest {
    private final SecretKey secretKey = Keys
            .hmacShaKeyFor(Base64.getDecoder().decode("d9qYD2WfPRNUOcydnzg4OGMvg+Am9+KpBuf2aEOnV2E="));

    @Autowired
    private JwtBlacklistService jwtBlacklistService;

    @Test
    void tokenIsAddedInBlacklistSuccess() throws Exception {
        // create a token
        // get the expirationDate of the token
        String user = "testUser@gmail.com";
        Date expirationDate = new Date(new Date().getTime() + 60 * 60 * 1000L);

        String token = Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();

        // act
        Boolean isBlackListedWithSuccess = jwtBlacklistService.addToBlackList(token, expirationDate);

        assertTrue(isBlackListedWithSuccess);
    }
}
