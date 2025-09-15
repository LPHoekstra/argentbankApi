package com.argentbank.argentbankApi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.argentbank.argentbankApi.exception.UnauthorizedException;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;

@Service
public class AuthService {
    
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    /**
     * Check if password is correct from the {@link LoginRequest}, and return a token if correct.
     * @param loginRequest
     * @return the auth token
     * @throws EntityNotFoundException
     * @throws UnauthorizedException
     */
    public String login(LoginRequest loginRequest) throws EntityNotFoundException {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("Can't find user with: " + loginRequest.getEmail());
        }

        // check if the password is correct
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        return jwtService.generateToken(user.getEmail());
    }

    public void logout(String token) {
        jwtService.invalidateToken(token);
    }
}
