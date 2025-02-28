package com.argentbank.argentbankApi.service;

import java.util.Date;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class JwtBlacklistService {
    private final ConcurrentHashMap<String, Long> blacklist = new ConcurrentHashMap<>();

    public void addToBlackList(String token, Date expirationDate) {
        if (expirationDate != null) {
            long expirationTime = expirationDate.getTime();
            blacklist.put(token, expirationTime);
            log.info("token {} black listed", token);
        }
    }

    public boolean isBlackListed(String token) {
        Long expirationTime = blacklist.get(token);

        if (expirationTime == null) {
            return false;
        }

        if (System.currentTimeMillis() >= expirationTime) {
            blacklist.remove(token);
            log.info("Expired token {} removed from blacklist", token);
            return false;
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
