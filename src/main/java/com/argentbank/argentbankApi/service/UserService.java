package com.argentbank.argentbankApi.service;

import com.argentbank.argentbankApi.exception.UnauthorizedException;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.repository.UserRepository;

import jakarta.persistence.EntityExistsException;
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
     * Check if password is correct from the {@link LoginRequest}
     * 
     * @param loginRequest
     * @return the user
     * @throws EntityNotFoundException
     * @throws UnauthorizedException
     */
    public User login(LoginRequest loginRequest) throws EntityNotFoundException {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("Can't find user with: " + loginRequest.getEmail());
        }

        // check if the password is correct
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        return user;
    }

    /**
     * 
     * @param signupRequest
     * @return the saved entity; will never be null.
     * @throws EntityExistsException
     */
    public User createUser(SignupRequest signupRequest) throws EntityExistsException {
        if (userRepository.existsByEmail(signupRequest.getEmail())) {
            throw new EntityExistsException("Email already used");
        }

        User user = new User();
        user.setEmail(signupRequest.getEmail());
        user.setPassword(passwordEncoder.encode(signupRequest.getPassword()));
        user.setFirstName(signupRequest.getFirstName());
        user.setLastName(signupRequest.getLastName());
        user.setUserName(signupRequest.getUserName());

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
