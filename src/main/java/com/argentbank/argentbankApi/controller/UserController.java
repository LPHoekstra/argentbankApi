package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.exception.HttpWithMsgException;
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

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtService jwtUtils;

    @Autowired
    public UserController(UserService userService, JwtService jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            log.debug("Login with email: {}", loginRequest.getEmail());
            User user = userService.login(loginRequest);

            String token = jwtUtils.generateToken(user.getEmail());

            log.info("Login successfully with email: {}", user.getEmail());
            return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully", new LoginResponse(token));

        } catch (HttpWithMsgException e) {
            log.warn("Failed login with email: {} error: {} {}", loginRequest.getEmail(), e.getStatus(),
                    e.getMessage());
            // return bad request for security purpose
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email or password", null);

        } catch (Exception e) {
            log.error(e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest signupRequest) {
        log.debug("Signup with email: {}", signupRequest.getEmail());
        boolean isEmailAldreadyExist = userService.createUser(signupRequest);

        if (!isEmailAldreadyExist) {
            log.warn("Tried signup with email: {} but email already used", signupRequest.getEmail());
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email", null);
        }

        log.info("Account with email: {} created with success", signupRequest.getEmail());
        return ResponseUtil.buildResponse(HttpStatus.OK, "Signup successfully", null);
    }

    @DeleteMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestHeader("Authorization") String token) {
        try {
            jwtUtils.invalidateToken(token);

            return ResponseUtil.buildResponse(HttpStatus.OK, "disconnected", null);
        } catch (BlackListedException e) {
            log.error(e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        } catch (Exception e) {
            log.error("Error on logout: {}", e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@RequestHeader("Authorization") String token) {
        try {
            log.debug("GET on /api/v1/users/profile");

            // check token and get email stored in token
            String email = jwtUtils.getUserFromToken(token);

            User user = userService.findUserByEmail(email);

            if (user == null) {
                log.warn("User not found");
                return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, "User not found", null);
            }

            log.info("Profile retrieved successfully from user: {}", user.getEmail());
            return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully",
                    new ProfileResponse(user));

        } catch (HttpWithMsgException e) {
            log.warn("error in GET profile: {} message: {}", e.getStatus(), e.getMessage());
            return ResponseUtil.buildResponse(e.getStatus(), e.getMessage(), null);

        } catch (Exception e) {
            log.error("Error in GET profile: {}", e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> changeProfile(@RequestHeader("Authorization") String token,
            @RequestBody ChangeProfileRequest ChangeProfileRequest) {
        try {
            log.debug("PUT on /api/v1/users/profile");

            // check token and get email stored in token
            String email = jwtUtils.getUserFromToken(token);

            // get the user then change his profile name
            User user = userService.findUserByEmail(email);
            userService.changeUser(user, ChangeProfileRequest.getUserName());

            log.info("Profile change successfully for email: {}", user.getEmail());
            return ResponseUtil.buildResponse(
                    HttpStatus.OK,
                    "User profile change successfully",
                    new ChangeProfileResponse(user.getUserName()));

        } catch (HttpWithMsgException e) {
            log.warn("error in PUT profile: {} message: {}", e.getStatus(), e.getMessage());
            return ResponseUtil.buildResponse(e.getStatus(), e.getMessage(), null);

        } catch (Exception e) {
            log.error("Error in PUT profile: {}", e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }
}
