package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.exception.MissingTokenException;
import com.argentbank.argentbankApi.exception.UnauthorizedException;
import com.argentbank.argentbankApi.model.*;
import com.argentbank.argentbankApi.model.request.ChangeProfileRequest;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.model.response.ApiResponse;
import com.argentbank.argentbankApi.model.response.ChangeProfileResponse;
import com.argentbank.argentbankApi.model.response.LoginResponse;
import com.argentbank.argentbankApi.model.response.ProfileResponse;
import com.argentbank.argentbankApi.service.JwtService;
import com.argentbank.argentbankApi.service.UserService;

import jakarta.persistence.EntityNotFoundException;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService) {
        this.userService = userService;
        this.jwtService = jwtService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest)
            throws EntityNotFoundException, UnauthorizedException {
        log.debug("Login with email: {}", loginRequest.getEmail());

        User user = userService.login(loginRequest);
        String token = jwtService.generateToken(user.getEmail());

        log.debug("Login successfully with email: {}", user.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully", new LoginResponse(token));
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.debug("Signup with email: {}", signupRequest.getEmail());

        userService.createUser(signupRequest);

        log.debug("Account with email: {} created with success", signupRequest.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "Signup successfully", null);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token)
            throws MissingTokenException, BlackListedException {
        if (token == null || token.isBlank()) {
            throw new MissingTokenException("Token is missing in request header");
        }

        jwtService.invalidateToken(token);
        return ResponseUtil.buildResponse(HttpStatus.OK, "disconnected", null);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@RequestHeader("Authorization") String token)
            throws BlackListedException {
        log.debug("getting profile from token: {}", token);

        String email = jwtService.getUserFromToken(token);
        User user = userService.findUserByEmail(email);

        ProfileResponse profileResponse = new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName());

        log.debug("Profile retrieved successfully from user: {}", user.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully", profileResponse);
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> changeProfile(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody ChangeProfileRequest ChangeProfileRequest) throws BlackListedException {
        log.debug("Updating profile with token: {}", token);

        String email = jwtService.getUserFromToken(token);
        User user = userService.findUserByEmail(email);
        User updatedUser = userService.changeUserName(user, ChangeProfileRequest.getUserName());

        ChangeProfileResponse changeProfileResponse = new ChangeProfileResponse(updatedUser.getUserName());

        log.debug("Profile change successfully for email: {}", updatedUser.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "User profile change successfully", changeProfileResponse);
    }
}
