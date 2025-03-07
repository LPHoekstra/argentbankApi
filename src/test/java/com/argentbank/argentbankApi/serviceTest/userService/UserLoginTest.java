package com.argentbank.argentbankApi.serviceTest.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.argentbank.argentbankApi.exception.HttpWithMsgException;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.repository.UserRepository;
import com.argentbank.argentbankApi.service.UserService;

@SpringBootTest
public class UserLoginTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void testLoginSuccess() throws Exception {
        String email = "test@gmail.com";
        String password = "password";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(password);

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(password));

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(true);

        // act
        User result = userService.login(loginRequest);

        assertNotNull(result);
        assertEquals(user, result);
    }

    @Test
    void testLoginUserNotFind() throws Exception {
        String email = "test@gmail.com";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(null);

        // act
        HttpWithMsgException exception = assertThrows(HttpWithMsgException.class,
                () -> userService.login(loginRequest));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatus());
        assertEquals("User not found", exception.getMessage());
    }

    @Test
    void testLoginIncorrectPassword() throws Exception {
        String email = "test@gmail.com";
        String invalidPassword = "password";
        String correctPassword = "password123";

        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setEmail(email);
        loginRequest.setPassword(invalidPassword);

        User user = new User();
        user.setEmail(email);
        user.setPassword(passwordEncoder.encode(correctPassword));

        when(userRepository.findByEmail(loginRequest.getEmail())).thenReturn(user);
        when(passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())).thenReturn(false);

        // act
        HttpWithMsgException exception = assertThrows(HttpWithMsgException.class,
                () -> userService.login(loginRequest));

        assertEquals(HttpStatus.UNAUTHORIZED, exception.getStatus());
        assertEquals("Invalid password", exception.getMessage());
    }
}
