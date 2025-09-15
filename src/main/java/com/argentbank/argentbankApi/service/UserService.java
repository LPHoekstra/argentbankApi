package com.argentbank.argentbankApi.service;

import com.argentbank.argentbankApi.exception.EmailUsedException;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

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

    /**
     * 
     * @param signupRequest
     * @return the saved entity; will never be null.
     */
    public User createUser(SignupRequest signupRequest) {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EmailUsedException("Email already used");
        }

        User user = new User(
            signupRequest.getEmail(),
            passwordEncoder.encode(signupRequest.getPassword()),
            signupRequest.getFirstName(),
            signupRequest.getLastName(),
            signupRequest.getUserName()
        );

        return userRepository.save(user);
    }

    public User findUserByEmail(String email) throws EntityNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new EntityNotFoundException("User not found in DB");
        }

        return user;
    }

    public User changeUserName(User user, String userName) {
        user.setUserName(userName);

        return userRepository.save(user);
    }
}
