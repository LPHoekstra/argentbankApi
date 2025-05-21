package com.argentbank.argentbankApi.serviceTest.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.repository.UserRepository;
import com.argentbank.argentbankApi.service.UserService;

import jakarta.persistence.EntityExistsException;

@SpringBootTest
public class CreateUserTest {

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void createdWithSuccess() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setFirstName("tom");
        signupRequest.setLastName("hoekstra");
        signupRequest.setPassword("password");
        signupRequest.setUserName("mot");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(false);
        when(passwordEncoder.encode(signupRequest.getPassword())).thenReturn("encodedPassword");

        // act
        User createdUser = userService.createUser(signupRequest);

        assertEquals(signupRequest.getLastName(), createdUser.getLastName());
        assertNotNull(createdUser);
        verify(userRepository, times(1)).save(any(User.class));
    }

    @Test
    void userAlreadyExist() throws Exception {
        SignupRequest signupRequest = new SignupRequest();
        signupRequest.setEmail("test@gmail.com");
        signupRequest.setFirstName("tom");
        signupRequest.setLastName("hoekstra");
        signupRequest.setPassword("password");
        signupRequest.setUserName("mot");

        when(userRepository.existsByEmail(signupRequest.getEmail())).thenReturn(true);

        // act
        assertThrows(EntityExistsException.class, () -> userService.createUser(signupRequest));
        verify(userRepository, times(0)).save(any(User.class));
    }
}
