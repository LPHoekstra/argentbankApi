package com.argentbank.argentbankApi.serviceTest.jwtBlacklistService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Base64;
import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.service.JwtBlacklistService;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@SpringBootTest
public class AddToBlackListTest {
    private final SecretKey secretKey = Keys
            .hmacShaKeyFor(Base64.getDecoder().decode("d9qYD2WfPRNUOcydnzg4OGMvg+Am9+KpBuf2aEOnV2E="));

    private JwtBlacklistService jwtBlacklistService;
    private ConcurrentHashMap<String, Long> blacklistMock = spy(new ConcurrentHashMap<>());

    private Date expirationDate = new Date(new Date().getTime() + 60 * 60 * 1000L);

    @BeforeEach
    void setUp() {
        jwtBlacklistService = new JwtBlacklistService(blacklistMock);
    }

    @Test
    void tokenIsAddedInBlacklistSuccess() throws Exception {
        String token = createTestToken(expirationDate);

        // act
        Boolean isBlackListedWithSuccess = jwtBlacklistService.addToBlackList(token, expirationDate);

        assertTrue(isBlackListedWithSuccess, "Expect to return true");
    }

    @Test
    void tokenIsNotAddedInBlacklist() throws Exception {
        long expirationTime = expirationDate.getTime();
        String token = createTestToken(expirationDate);

        when(blacklistMock.put(token, expirationTime)).thenReturn(123456789L);

        // act
        Boolean isBlackListed = jwtBlacklistService.addToBlackList(token, expirationDate);

        assertFalse(isBlackListed, "Expect to return false");
    }

    private String createTestToken(Date expirationDate) {
        String user = "testUser@gmail.com";

        return Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(expirationDate)
                .signWith(secretKey)
                .compact();
    }
}
