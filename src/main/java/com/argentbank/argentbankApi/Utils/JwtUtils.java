package com.argentbank.argentbankApi.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.exception.HttpWithMsgException;
import com.argentbank.argentbankApi.service.JwtBlacklistService;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Component
public class JwtUtils {

    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    private Jws<Claims> verifyToken(String token) {
        try {
            Boolean isBlackListed = JwtBlacklistService.isBlackListed(token);
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
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Invalid Token");
        } catch (BlackListedException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, e.getMessage());
        } catch (Exception e) {
            throw new RuntimeException("Error on token validation : " + e.getMessage());
        }
    }

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

    public String getUserFromToken(String token) {
        String tokenValue = extractToken(token);

        Jws<Claims> claims = verifyToken(tokenValue);

        return claims.getPayload().getSubject();
    }

    public void invalidateToken(String token) {
        String tokenValue = extractToken(token);
        JwtBlacklistService.addToBlackList(tokenValue);
    }

    public Date getExpirationDate(String token) {
        Claims claims = Jwts.parser().verifyWith(secretKey).build().parseSignedClaims(token).getPayload();

        return claims.getExpiration();
    }

    // remove "bearer " from token
    private String extractToken(String token) {
        String value = token.substring(7);

        return value;
    }
}
