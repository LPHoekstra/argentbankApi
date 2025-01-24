package com.argentbank.argentbankApi.Utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    // private final String JWT_SECRET = "secret_key";
    private final SecretKey secretKey = Jwts.SIG.HS256.key().build();

    public String generateToken(String user) {
        long JWT_EXPIRATION = 86400;
        return Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JWT_EXPIRATION))
                .signWith(secretKey)
                .compact();
    }

    public String getUserFromToken(String token) {
        String value = token.substring(7);

        Claims claims = Jwts.parser()
                .verifyWith(secretKey)
                .build()
                .parseSignedClaims(value)
                .getPayload();

        return claims.getSubject();
    }
}
