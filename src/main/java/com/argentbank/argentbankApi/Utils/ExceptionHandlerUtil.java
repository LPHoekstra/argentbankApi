package com.argentbank.argentbankApi.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.argentbank.argentbankApi.exception.BlackListedException;
import com.argentbank.argentbankApi.exception.MissingTokenException;
import com.argentbank.argentbankApi.exception.UnauthorizedException;
import com.argentbank.argentbankApi.model.response.ApiResponse;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@ControllerAdvice
public class ExceptionHandlerUtil {

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<ApiResponse> unauthorized(UnauthorizedException e) {
        log.error(e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Non autorisé", null);
    }

    @ExceptionHandler(EntityExistsException.class)
    public ResponseEntity<ApiResponse> entityAlreadyExist(EntityExistsException e) {
        log.error(e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Ressource déjà utilisé", null);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<ApiResponse> entityNotFound(EntityNotFoundException e) {
        log.error("Entity not found: {}", e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.NOT_FOUND, "Utilisateur non trouvé", null);
    }

    @ExceptionHandler(MissingTokenException.class)
    public ResponseEntity<ApiResponse> missingToken(MissingTokenException e) {
        log.error(e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Token manquant", null);
    }

    @ExceptionHandler(BlackListedException.class)
    public ResponseEntity<ApiResponse> blacklistedException(BlackListedException e) {
        log.error(e.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Token invalid", null);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse> validationException(MethodArgumentNotValidException ex) {
        log.error("Invalide request", ex.getMessage());
        return ResponseUtil.buildResponse(HttpStatus.BAD_REQUEST, "Invalid field", null);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiResponse> illegalArgumentException(IllegalArgumentException e) {
        log.error("Illegal argument: {}", e);
        return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse> notHandlException(Exception e) {
        log.error("Server error: {}", e);
        return ResponseUtil.buildResponse(HttpStatus.INTERNAL_SERVER_ERROR, "Server error", null);
    }

    // JWT Exception
    @ExceptionHandler(JwtException.class)
    public ResponseEntity<ApiResponse> jwtException(JwtException e) {
        log.error("Token error: {}", e);
        return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Token error", null);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public ResponseEntity<ApiResponse> unsupportedJwtException(UnsupportedJwtException e) {
        log.error("Token error: {}", e);
        return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Token error", null);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public ResponseEntity<ApiResponse> expiredJwtException(ExpiredJwtException e) {
        log.error("Token error: {}", e);
        return ResponseUtil.buildResponse(HttpStatus.UNAUTHORIZED, "Token expired", null);
    }
}
