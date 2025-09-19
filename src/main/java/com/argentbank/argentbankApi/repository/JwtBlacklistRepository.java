package com.argentbank.argentbankApi.repository;

import org.springframework.data.mongodb.repository.MongoRepository;
import com.argentbank.argentbankApi.model.TokenBlacklist;

public interface JwtBlacklistRepository extends MongoRepository<TokenBlacklist, String> {
    TokenBlacklist findByToken(String token);

    void deleteByExpirationTimeBefore(long currentTime);
}
