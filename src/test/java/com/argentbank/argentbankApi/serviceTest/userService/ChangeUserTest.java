package com.argentbank.argentbankApi.serviceTest.userService;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;

import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.repository.UserRepository;
import com.argentbank.argentbankApi.service.UserService;

@SpringBootTest
public class ChangeUserTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void changeUserSuccess() throws Exception {
        String changedUserName = "username";

        User user = new User();
        user.setUserName("defaultUsername");

        // act
        User updatedUser = userService.changeUserName(user, changedUserName);

        verify(userRepository, times(1)).save(any(User.class));
        assertNotNull(updatedUser);
        assertEquals(changedUserName, updatedUser.getUserName());
    }
}
