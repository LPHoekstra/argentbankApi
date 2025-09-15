package com.argentbank.argentbankApi.serviceTest.userService;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.repository.UserRepository;
import com.argentbank.argentbankApi.service.UserService;

@SpringBootTest
public class FindUserByEmailTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void findUser() throws Exception {
        String email = "test@gmail.com";

        User user = new User();

        when(userRepository.findByEmail(email)).thenReturn(user);

        // act
        User findUser = userService.getByEmail(email);

        assertNotNull(findUser);
    }
}
