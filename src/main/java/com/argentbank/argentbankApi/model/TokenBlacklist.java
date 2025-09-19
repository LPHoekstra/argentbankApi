package com.argentbank.argentbankApi.model;

import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "tokenBlacklist")
public class TokenBlacklist {
    private String token;
    private long expirationTime;
    
    public TokenBlacklist() {}

    public TokenBlacklist(String token, long expirationTime) {
        this.token = token;
        this.expirationTime = expirationTime;
    }

    public String getToken() {
        return this.token;
    }

    public long getExpirationTime() {
        return this.expirationTime;
    }
}
