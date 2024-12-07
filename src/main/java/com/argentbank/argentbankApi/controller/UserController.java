package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.JwtUtils;
import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.model.*;
import com.argentbank.argentbankApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, AuthenticationManager authenticationManager, JwtUtils jwtUtils) {
        this.userService = userService;
        this.authenticationManager = authenticationManager;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
        boolean isAuthenticated = userService.authenticate(loginRequest.getEmail(), loginRequest.getPassword());
        if (!isAuthenticated) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email or password", null);
        }

        String token = jwtUtils.generateToken(loginRequest.getEmail());
        // must return the jsonwebtoken
        return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully", new LoginResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody SignupRequest signupRequest) {
        boolean isSignup = userService.createUser(signupRequest);
        if (!isSignup) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email", null);
        }

        return ResponseUtil.buildResponse(HttpStatus.OK, "Signup successfully", null);
    }

    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(@RequestHeader("Authorization") String token) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Unauthorized access", null);
        }

        try {
            String email = jwtUtils.getUserFromToken(token);

            User user = userService.findUserByEmail(email);

            if (user == null) {
                return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, "User not found", null);
            }

            return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully", new ProfileResponse(user));
        } catch (Exception e) {
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", null);
        }
    }
}
