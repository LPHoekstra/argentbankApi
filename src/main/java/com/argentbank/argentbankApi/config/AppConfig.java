package com.argentbank.argentbankApi.config;

import java.util.concurrent.ConcurrentHashMap;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {

    @Bean
    public ConcurrentHashMap<String, Long> blacklistMap() {
        return new ConcurrentHashMap<>();
    }
}
