// package com.argentbank.argentbankApi.serviceTest.jwtBlacklistService;

// import static org.junit.jupiter.api.Assertions.assertFalse;
// import static org.junit.jupiter.api.Assertions.assertTrue;
// import static org.mockito.Mockito.spy;
// import static org.mockito.Mockito.when;

// import java.util.Date;
// import java.util.concurrent.ConcurrentHashMap;

// import org.junit.jupiter.api.BeforeEach;
// import org.junit.jupiter.api.Test;
// import org.springframework.boot.test.context.SpringBootTest;

// import com.argentbank.argentbankApi.security.JwtBlacklist;

// @SpringBootTest
// public class IsBlackListedTest {
//     private JwtBlacklist jwtBlacklistService;
//     private ConcurrentHashMap<String, Long> blacklistMock = spy(new ConcurrentHashMap<>());

//     @BeforeEach
//     void setUp() {
//         jwtBlacklistService = new JwtBlacklist(blacklistMock);
//     }

//     @Test
//     void tokenIsBlacklisted() throws Exception {
//         String token = "test.token";
//         // expire in one hour
//         Long expirationTime = new Date().getTime() + 60 * 60 * 1000L;

//         when(blacklistMock.get(token)).thenReturn(expirationTime);

//         // act
//         Boolean isBlacklisted = jwtBlacklistService.isBlackListed(token);

//         assertTrue(isBlacklisted, "Token must be in the blacklist");
//     }

//     @Test
//     void tokenIsNotBlacklisted() throws Exception {
//         String token = "test.token";

//         when(blacklistMock.get(token)).thenReturn(null);
//         // act
//         Boolean isBlacklisted = jwtBlacklistService.isBlackListed(token);

//         assertFalse(isBlacklisted, "token is not blacklisted");
//     }

//     @Test
//     void tokenIsExpiredAndRemovedFromBlacklist() throws Exception {
//         String token = "test.token";
//         // expired one minute ago
//         Long expirationTime = new Date().getTime() - 60 * 1000L;

//         when(blacklistMock.get(token)).thenReturn(expirationTime);
//         when(blacklistMock.remove(token)).thenReturn(expirationTime);

//         // act
//         Boolean isBlacklisted = jwtBlacklistService.isBlackListed(token);

//         assertFalse(isBlacklisted, "token is removed from the blacklist because he expired");
//     }
// }
