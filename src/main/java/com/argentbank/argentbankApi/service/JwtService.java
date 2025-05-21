package com.argentbank.argentbankApi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.argentbank.argentbankApi.exception.BlackListedException;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    private SecretKey secretKey;

    private final JwtBlacklistService jwtBlacklistService;

    public JwtService(JwtBlacklistService jwtBlacklistService, @Value("${jwt.secret}") String secret) {
        this.jwtBlacklistService = jwtBlacklistService;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    /**
     * 
     * @param user email
     * @return the token in String
     */
    public String generateToken(String user) {
        // token valide for one hour
        long JWT_EXPIRATION = 60 * 60 * 1000L;
        return Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 
     * @param token with "Bearer "
     * @return the user email : "test@gmail.com"
     */
    public String getUserFromToken(String token) throws BlackListedException {
        return verifyToken(extractToken(token)).getPayload().getSubject();
    }

    /**
     * 
     * @param token with "Bearer "
     */
    public void invalidateToken(String token) throws BlackListedException {
        String tokenValue = extractToken(token);
        Date expirationDate = verifyToken(tokenValue).getPayload().getExpiration();

        Boolean isBlacklisted = jwtBlacklistService.addToBlackList(tokenValue, expirationDate);
        if (!isBlacklisted) {
            throw new BlackListedException("Token already blacklisted");
        }

        log.info("token {} black listed", token);
    }

    /**
     * 
     * @param token only without "Bearer "
     * @return
     */
    private Jws<Claims> verifyToken(String token)
            throws UnsupportedJwtException, JwtException, IllegalArgumentException, BlackListedException {
        Boolean isBlackListed = jwtBlacklistService.isBlackListed(token);
        if (isBlackListed) {
            throw new BlackListedException("Token is blacklisted");
        }

        Jws<Claims> claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(token);

        return claims;
    }

    /**
     * remove "bearer " from token
     * 
     * @param token
     * @return
     */
    private String extractToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new RuntimeException("Invalid token format, does not have 'Bearer '");
        }

        return token.substring(7);
    }

    /**
     * Getters for test only (to delete?)
     * 
     * @return
     */
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}
