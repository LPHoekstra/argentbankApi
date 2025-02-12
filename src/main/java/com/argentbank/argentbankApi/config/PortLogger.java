package com.argentbank.argentbankApi.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
public class PortLogger implements ApplicationRunner {
    @Value("${server.port}")
    private String port;

    public void run(ApplicationArguments arguments) {
        System.out.println("Server running on port : " + port);
    }

}
