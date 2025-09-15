package com.argentbank.argentbankApi.service;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.argentbank.argentbankApi.exception.UnauthorizedException;
import com.argentbank.argentbankApi.model.User;
import com.argentbank.argentbankApi.model.request.LoginRequest;
import com.argentbank.argentbankApi.repository.UserRepository;

import jakarta.persistence.EntityNotFoundException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class AuthService {
    // TODO add front domain to app.properties
    private static final String FRONT_DOMAIN = "localhost";
    public static final String AUTH_TOKEN = "authToken";
    
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
     * @return the auth token as a cookie
     * @throws EntityNotFoundException
     * @throws UnauthorizedException
     */
    public void login(LoginRequest loginRequest, HttpServletResponse servletResponse) throws EntityNotFoundException {
        User user = userRepository.findByEmail(loginRequest.getEmail());
        if (user == null) {
            throw new EntityNotFoundException("Can't find user with: " + loginRequest.getEmail());
        }

        // check if the password is correct
        if (!passwordEncoder.matches(loginRequest.getPassword(), user.getPassword())) {
            throw new UnauthorizedException("Invalid password");
        }

        String token = jwtService.generateToken(user.getEmail());
        Cookie authTokenCookie = generateAuthTokenCookie(token);
        Cookie isAuthCookie = generateIsAuthCookie();
        servletResponse.addCookie(authTokenCookie);
        servletResponse.addCookie(isAuthCookie);
    }

    public void logout(String token, HttpServletResponse servletResponse) {
        servletResponse.addCookie(invalidateCookie(AUTH_TOKEN));
        servletResponse.addCookie(invalidateCookie("isAuth"));
        
        jwtService.invalidateToken(token);
    }

    private Cookie invalidateCookie(String cookieName) {
        Cookie cookie = new Cookie(cookieName, "");
        cookie.setMaxAge(0);
        cookie.setDomain(FRONT_DOMAIN);
        cookie.setPath("/");

        return cookie;
    }

    private Cookie generateAuthTokenCookie(String token) {
        Cookie cookie = new Cookie(AUTH_TOKEN, token);
        cookie.setHttpOnly(true);
        cookie.setMaxAge(Math.toIntExact(JwtService.JWT_EXPIRATION / 1000l));
        cookie.setSecure(true);
        cookie.setDomain(FRONT_DOMAIN);
        cookie.setPath("/");

        return cookie;
    }

    private Cookie generateIsAuthCookie() {
        Cookie cookie = new Cookie("isAuth", "1");
        cookie.setHttpOnly(false);
        cookie.setMaxAge(Math.toIntExact(JwtService.JWT_EXPIRATION / 1000l));
        cookie.setDomain(FRONT_DOMAIN);
        cookie.setPath("/");

        return cookie;
    }
}
