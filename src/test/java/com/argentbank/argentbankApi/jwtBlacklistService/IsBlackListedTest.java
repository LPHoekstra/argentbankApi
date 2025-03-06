package com.argentbank.argentbankApi.jwtBlacklistService;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.service.JwtBlacklistService;

@SpringBootTest
public class IsBlackListedTest {
    private JwtBlacklistService jwtBlacklistService;
    private ConcurrentHashMap<String, Long> blacklistMock = spy(new ConcurrentHashMap<>());

    @BeforeEach
    void setUp() {
        jwtBlacklistService = new JwtBlacklistService(blacklistMock);
    }

    @Test
    void isBlacklistedWithSuccess() throws Exception {
        String token = "test.token";
        Long expirationTime = new Date().getTime() + 60 * 1000L;

        when(blacklistMock.get(token)).thenReturn(expirationTime);

        // act
        Boolean isBlacklisted = jwtBlacklistService.isBlackListed(token);

        assertTrue(isBlacklisted, "Token must be in the blacklist");
    }

    @Test
    void tokenIsNotBlacklisted() throws Exception {
        String token = "test.token";

        when(blacklistMock.get(token)).thenReturn(null);
        // act
        Boolean isBlacklisted = jwtBlacklistService.isBlackListed(token);

        assertFalse(isBlacklisted, "token is not blacklisted");
    }
}
