package com.argentbank.argentbankApi.service;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.exception.HttpWithMsgException;

import javax.crypto.SecretKey;

import java.util.Base64;
import java.util.Date;

@Slf4j
@Service
public class JwtService {
    private SecretKey secretKey;

    private final JwtBlacklistService jwtBlacklistService;

    @Autowired
    public JwtService(JwtBlacklistService jwtBlacklistService, @Value("${jwt.secret}") String secret) {
        this.jwtBlacklistService = jwtBlacklistService;
        this.secretKey = Keys.hmacShaKeyFor(Base64.getDecoder().decode(secret));
    }

    /**
     * 
     * @param user email
     * @return
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
    public String getUserFromToken(String token) {
        return verifyToken(extractToken(token)).getPayload().getSubject();
    }

    /**
     * 
     * @param token with "Bearer "
     */
    public void invalidateToken(String token) {
        String tokenValue = extractToken(token);
        Date expirationDate = verifyToken(tokenValue).getPayload().getExpiration();

        jwtBlacklistService.addToBlackList(tokenValue, expirationDate);
    }

    /**
     * 
     * @param token only without "Bearer "
     * @return
     */
    private Jws<Claims> verifyToken(String token) {
        try {
            Boolean isBlackListed = jwtBlacklistService.isBlackListed(token);
            if (isBlackListed) {
                throw new BlackListedException("Token is blacklisted");
            }

            Jws<Claims> claims = Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(token);

            return claims;
        } catch (ExpiredJwtException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (MalformedJwtException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Malformed token");
        } catch (SignatureException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Invalid token");
        } catch (BlackListedException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error on token validation : " + e.getMessage());
        }
    }

    // remove "bearer " from token
    private String extractToken(String token) {
        if (token == null || !token.startsWith("Bearer ")) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Invalid token format");
        }

        return token.substring(7);
    }

    // getters for test
    public SecretKey getSecretKey() {
        return this.secretKey;
    }
}
