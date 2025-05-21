package com.argentbank.argentbankApi.service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.argentbank.argentbankApi.exception.BlackListedException;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtBlacklistService {
    private final ConcurrentHashMap<String, Long> blacklist;

    public JwtBlacklistService(ConcurrentHashMap<String, Long> blacklist) {
        this.blacklist = blacklist;
    }

    /**
     * 
     * @param token
     * @param expirationDate of the token
     * @return a boolean if the token is blacklisted with success or not
     */
    public Boolean addToBlackList(String token, Date expirationDate) {
        long expirationTime = expirationDate.getTime();
        Long isBlacklisted = blacklist.put(token, expirationTime);

        if (isBlacklisted != null) {
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
        Long expirationTime = blacklist.get(token);

        // if the token is not blacklisted
        if (expirationTime == null) {
            return false;
        }

        // is token expired
        if (System.currentTimeMillis() >= expirationTime) {
            Long tokenRemovedFromBlacklist = blacklist.remove(token);

            if (tokenRemovedFromBlacklist != null) {
                log.info("Expired token {} removed from blacklist", tokenRemovedFromBlacklist);
                return false;
            }

            throw new BlackListedException("error in blacklist remove");
        }

        log.info("token {} is blacklisted", token);
        return true;
    }

    @Scheduled(fixedRate = 30, timeUnit = TimeUnit.MINUTES)
    public void cleanExpiredToken() {
        long currentTime = System.currentTimeMillis();
        blacklist.entrySet().removeIf(entry -> entry.getValue() <= currentTime);
        log.info("Expired token cleaned");
    }
}
