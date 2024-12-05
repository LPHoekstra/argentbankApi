package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.JwtUtils;
import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.model.LoginRequest;
import com.argentbank.argentbankApi.model.LoginResponse;
import com.argentbank.argentbankApi.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
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
        if (isAuthenticated) {
            return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", null);
        }

        String token = jwtUtils.generateToken(loginRequest.getEmail());
        // must return the jsonwebtoken
        return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully", new LoginResponse(token));
    }

//    @GetMapping("/profile")
//    public ResponseEntity<?> getProfile() {
//
//        if (isAuthenticated) {
//            return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Invalid email or password", null);
//        }
//
//        return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully", new User());
//    }
}
