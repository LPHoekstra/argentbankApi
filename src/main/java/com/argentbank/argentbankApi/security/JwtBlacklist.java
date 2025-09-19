package com.argentbank.argentbankApi.security;

import java.util.Date;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.model.TokenBlacklist;
import com.argentbank.argentbankApi.repository.JwtBlacklistRepository;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class JwtBlacklist {
    private final JwtBlacklistRepository jwtBlacklistRepository;

    public JwtBlacklist(JwtBlacklistRepository jwtBlacklistRepository) {
        this.jwtBlacklistRepository = jwtBlacklistRepository;
    }

    /**
     * 
     * @param token
     * @param expirationDate of the token
     * @return a boolean if the token is blacklisted with success or not
     */
    public Boolean add(String token, Date expirationDate) {
        long expirationTime = expirationDate.getTime();
        TokenBlacklist tokenBlacklist = new TokenBlacklist(token, expirationTime);
        TokenBlacklist savedToken = jwtBlacklistRepository.save(tokenBlacklist);

        if (savedToken != null) {
            return false;
        }

        return true;
    }

    /**
     * Check if a token is blacklisted or not.
     * 
     * @param token as value
     * @return true if the token is currently backlisted. Otherwise return false if
     *         the token is not blacklisted or if he's removed from the blacklist.
     */
    public boolean isBlackListed(String token) throws BlackListedException {
        TokenBlacklist retrievedToken = jwtBlacklistRepository.findByToken(token);

        // if the token is not blacklisted
        if (retrievedToken == null) {
            return false;
        }

        // is token expired
        if (System.currentTimeMillis() >= retrievedToken.getExpirationTime()) {
            jwtBlacklistRepository.delete(retrievedToken);

            log.info("Expired token {} removed from blacklist", retrievedToken.getToken());
            return false;
        }

        log.info("token {} is blacklisted", token);
        return true;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void cleanExpiredToken() {
        long currentTime = System.currentTimeMillis();
        jwtBlacklistRepository.deleteByExpirationTimeBefore(currentTime);
        log.info("Expired token cleaned");
    }
}
