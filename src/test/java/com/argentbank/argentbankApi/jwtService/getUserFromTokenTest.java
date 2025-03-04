package com.argentbank.argentbankApi.jwtService;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.service.JwtService;

@SpringBootTest
public class getUserFromTokenTest {

    @Autowired
    private JwtService jwtService;

    @Test
    void getUserFromTokenSuccess() throws Exception {
        String userEmail = "Arthur@gmail.com";
        String token = jwtService.generateToken(userEmail);
        String BearerToken = "Bearer " + token;

        // act
        String userEmailFromToken = jwtService.getUserFromToken(BearerToken);

        assertEquals(userEmail, userEmailFromToken, "User extract from token must be correct");
    }
}
