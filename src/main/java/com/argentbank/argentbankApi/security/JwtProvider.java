package com.argentbank.argentbankApi.security;

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
public class JwtProvider {
    private SecretKey secretKey;
    // one hour expiration
    public final static long JWT_EXPIRATION = 60 * 60 * 1000L;

    private final JwtBlacklist jwtBlacklistService;

    public JwtProvider(JwtBlacklist jwtBlacklistService, @Value("${jwt.secret}") String secret) {
        this.jwtBlacklistService = jwtBlacklistService;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    /**
     * 
     * @param userEmail email
     * @return the token in String
     */
    public String generateToken(String userEmail) {
        return Jwts.builder()
                .subject(userEmail)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    /**
     * 
     * @param token
     * @return the user email : "test@gmail.com"
     */
    public String getEmailFromToken(String token) throws BlackListedException {
        return verifyToken(token).getPayload().getSubject();
    }

    /**
     * return true if the token as been invalidate with success otherwise it return false.
     * @param token
     */
    public boolean invalidateToken(String token) {
        try {
            Date expirationDate = verifyToken(token).getPayload().getExpiration();
            
            Boolean isBlacklisted = jwtBlacklistService.add(token, expirationDate);
            if (!isBlacklisted) {
                throw new BlackListedException("Token already blacklisted");
            }

            log.info("token {} black listed", token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 
     * @param token
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
     * Getters for test only (to delete?)
     * 
     * @return
     */
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}
