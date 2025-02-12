package com.argentbank.argentbankApi.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.security.SignatureException;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.argentbank.argentbankApi.exception.HttpWithMsgException;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    private boolean verifyToken(String Token) {
        try {
            Jwts.parser()
                    .verifyWith(secretKey)
                    .build()
                    .parseSignedClaims(Token);

            return true;
        } catch (ExpiredJwtException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Token expired");
        } catch (MalformedJwtException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Malformed token");
        } catch (SignatureException e) {
            throw new HttpWithMsgException(HttpStatus.UNAUTHORIZED, "Invalid Token");
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
        // remove "bearer " from token
        String value = token.substring(7);

        verifyToken(value);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(value)
                .getPayload();

        return claims.getSubject();
    }
}
