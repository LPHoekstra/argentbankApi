package com.argentbank.argentbankApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class PortLogger implements ApplicationRunner {
    @Value("${server.port}")
    private String port;

    public void run(ApplicationArguments arguments) {
        log.info("Server running on port : " + port);
    }

}
