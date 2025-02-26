package com.argentbank.argentbankApi.service;

import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User authenticate(LoginRequest loginRequest) {
        // if no user is find, return false
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new RuntimeException("User not find");
        }

        // check if the password is correct
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid password");
        }

        return user;
    }

    public boolean createUser(SignupRequest signupRequest) {
        // if the user already exist return false
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            return false;
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUserName(signupRequest.getUserName());

        userRepository.save(user);

        return true;
    }

    public User findUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    public boolean changeUser(User user, String userName) {
        user.setUserName(userName);
        userRepository.save(user);

        return true;
    }
}
