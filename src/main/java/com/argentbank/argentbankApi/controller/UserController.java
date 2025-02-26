package com.argentbank.argentbankApi.controller;

import com.argentbank.argentbankApi.Utils.JwtUtils;
import com.argentbank.argentbankApi.Utils.ResponseUtil;
import com.argentbank.argentbankApi.exception.HttpWithMsgException;
import com.argentbank.argentbankApi.model.*;
import com.argentbank.argentbankApi.model.request.ChangeProfileRequest;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.model.request.SignupRequest;
import com.argentbank.argentbankApi.model.response.ApiResponse;
import com.argentbank.argentbankApi.model.response.ChangeProfileResponse;
import com.argentbank.argentbankApi.model.response.LoginResponse;
import com.argentbank.argentbankApi.model.response.ProfileResponse;
import com.argentbank.argentbankApi.service.UserService;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/user")
public class UserController {

    private final UserService userService;
    private final JwtUtils jwtUtils;

    public UserController(UserService userService, JwtUtils jwtUtils) {
        this.userService = userService;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/login")
    public ResponseEntity<ApiResponse> login(@RequestBody LoginRequest loginRequest) {
        try {
            User user = userService.authenticate(loginRequest);

            String token = jwtUtils.generateToken(user.getEmail());

            return ResponseUtil.buildResponse(HttpStatus.OK, "Login successfully", new LoginResponse(token));
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email or password", null);
        }
    }

    @PostMapping("/signup")
    public ResponseEntity<ApiResponse> signup(@RequestBody SignupRequest signupRequest) {
        boolean isEmailAldreadyExist = userService.createUser(signupRequest);

        if (!isEmailAldreadyExist) {
            return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid email", null);
        }

        return ResponseUtil.buildResponse(HttpStatus.OK, "Signup successfully", null);
    }

    @GetMapping("/profile")
    public ResponseEntity<ApiResponse> getProfile(@RequestHeader("Authorization") String token) {
        try {
            // check token and get email stored in token
            String email = jwtUtils.getUserFromToken(token);

            User user = userService.findUserByEmail(email);

            if (user == null) {
                return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, "User not found", null);
            }

            return ResponseUtil.buildResponse(HttpStatus.OK, "User profile retrieved successfully",
                    new ProfileResponse(user));
        } catch (HttpWithMsgException e) {
            System.out.println(e.geStatus());
            System.out.println(e.getMessage());
            return ResponseUtil.buildResponse(e.geStatus(), e.getMessage(), null);
        } catch (Exception e) {
            System.out.println("Error in GET profile");
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }

    @PutMapping("/profile")
    public ResponseEntity<ApiResponse> changeProfile(@RequestHeader("Authorization") String token,
            @RequestBody ChangeProfileRequest ChangeProfileRequest) {
        try {
            // check token and get email stored in token
            String email = jwtUtils.getUserFromToken(token);

            // get the user then change his profile name
            User user = userService.findUserByEmail(email);
            userService.changeUser(user, ChangeProfileRequest.getUserName());

            return ResponseUtil.buildResponse(
                    HttpStatus.OK,
                    "User profile change successfully",
                    new ChangeProfileResponse(user.getUserName()));

        } catch (HttpWithMsgException e) {
            System.out.println(e.geStatus());
            System.out.println(e.getMessage());
            return ResponseUtil.buildResponse(e.geStatus(), e.getMessage(), null);

        } catch (Exception e) {
            System.out.println("Error in PUT profile");
            return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Error server", null);
        }
    }
}
