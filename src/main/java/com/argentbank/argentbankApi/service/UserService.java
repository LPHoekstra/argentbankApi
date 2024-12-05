package com.argentbank.argentbankApi.service;

import com.argentbank.argentbankApi.Utils.JwtUtils;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.repository.UserRepository;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;

    public UserService(UserRepository userRepository, JwtUtils jwtUtils) {
        this.userRepository = userRepository;
        this.jwtUtils = jwtUtils;
    }

    public boolean authenticate(String email, String password) {
        User user = userRepository.findByEmail(email);
        if (user == null) {
            return false;
        }
        return new BCryptPasswordEncoder().encode(password).equals(user.getPassword());
    }
}
