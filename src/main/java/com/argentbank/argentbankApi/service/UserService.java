package com.argentbank.argentbankApi.service;

import com.argentbank.argentbankApi.Utils.JwtUtils;
import com.argentbank.argentbankApi.model.SignupRequest;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, JwtUtils jwtUtils, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean authenticate(String email, String password) {
        // if no user is find, return false
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }

        // check if the password is correct
        if (!passwordEncoder.matches(password, user.getPassword())) {
            return false;
        }

        return true;
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
}
