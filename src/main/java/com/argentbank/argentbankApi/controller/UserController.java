package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.model.*;
import com.argentbank.argentbankApi.model.request.ChangeProfileRequest;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.model.response.ApiResponse;
import com.argentbank.argentbankApi.model.response.ChangeProfileResponse;
import com.argentbank.argentbankApi.model.response.ProfileResponse;
import com.argentbank.argentbankApi.service.AuthService;
import com.argentbank.argentbankApi.service.JwtService;
import com.argentbank.argentbankApi.service.UserService;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

// TODO implement a kind of pipeline which is checking the auth token before on secure route
@Validated
@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {
    private final AuthService authService;
    private final UserService userService;
    private final JwtService jwtService;

    public UserController(UserService userService, JwtService jwtService, AuthService authService) {
        this.userService = userService;
        this.jwtService = jwtService;
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse servletResponse) {
        log.debug("Login with email: {}", loginRequest.getEmail());

        authService.login(loginRequest, servletResponse);

        log.debug("Login successfully with email: {}", loginRequest.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully");
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@Valid @RequestBody SignupRequest signupRequest) {
        log.debug("Signup with email: {}", signupRequest.getEmail());

        userService.createUser(signupRequest);

        log.debug("Account with email: {} created with success", signupRequest.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "Signup successfully", null);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@CookieValue(AuthService.AUTH_TOKEN) String token, HttpServletResponse servletResponse) {

        authService.logout(token, servletResponse);

        return ResponseUtil.buildResponse(HttpStatus.OK, "disconnected", null);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@CookieValue(AuthService.AUTH_TOKEN) String token) {
        log.debug("getting profile from token: {}", token);

        String email = jwtService.getEmailFromToken(token);
        User user = userService.getByEmail(email);

        log.debug("Profile retrieved successfully from user: {}", user.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully", new ProfileResponse(
                user.getId(),
                user.getEmail(),
                user.getFirstName(),
                user.getLastName(),
                user.getUserName()
            ));
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> changeProfile(
            @CookieValue(AuthService.AUTH_TOKEN) String token,
            @Valid @RequestBody ChangeProfileRequest ChangeProfileRequest) {
        log.debug("Updating profile with token: {}", token);

        String email = jwtService.getEmailFromToken(token);
        User user = userService.getByEmail(email);
        User updatedUser = userService.changeUserName(user, ChangeProfileRequest.getUserName());

        log.debug("Profile change successfully for email: {}", updatedUser.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "User profile change successfully", new ChangeProfileResponse(updatedUser.getUserName()));
    }
}
