package com.argentbank.argentbankApi.Utils;

import io.jsonwebtoken.Jwts;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;

@Component
public class JwtUtils {

    private final String JWT_SECRET = "secret_key";
    private final long JWT_EXPIRATION = 86400;
    private final SecretKey key = Jwts.SIG.HS256.key().build();

    public String generateToken(String user) {
        return Jwts.builder()
                .subject(user)
                .issuedAt(new Date())
                .expiration(new Date(new Date().getTime() + JWT_EXPIRATION))
                .signWith(key)
                .compact();
    }

    public boolean getUserFromToken(String token, String user) {
        return Jwts.parser()
                .verifyWith(key)
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getSubject()
                .equals(user);
    }
}
