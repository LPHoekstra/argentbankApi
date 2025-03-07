package com.argentbank.argentbankApi.controllerTest.userController;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.argentbank.argentbankApi.service.JwtService;

@SpringBootTest
@AutoConfigureMockMvc
public class logoutTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void tokenIsMissing() throws Exception {
        String token = "";

        // act
        mockMvc.perform(delete("/api/v1/user/logout")
                .header("Authorization", token))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Missing token"));

        // verify
        verify(jwtService, times(0)).invalidateToken(anyString());
    }

    @Test
    void tokenIsSuccessfullyInvalidate() throws Exception {
        String token = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJtYWxib3VyYmllQGdtYWlsLmNvbSIsImlhdCI6MTc0MTM0NjkxNiwiZXhwIjoxNzQxMzUwNTE2fQ.KN1WNVP7Wj3npbo6OAp-f8rG3xIUWmbKCmuHaoZWb4A";

        // act
        mockMvc.perform(delete("/api/v1/user/logout")
                .header("Authorization", token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("disconnected"));

        // verify
        verify(jwtService, times(1)).invalidateToken(token);
    }

    @Test
    void tokenIsAlreadyBlacklisted() throws Exception {

    }
}
