package com.argentbank.argentbankApi.Utils;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.argentbank.argentbankApi.model.response.ApiResponse;

public class ResponseUtil {
    public static ResponseEntity<ApiResponse> buildResponse(HttpStatus httpStatus, String message, Object data) {
        return ResponseEntity.status(httpStatus)
                .body(new ApiResponse(httpStatus.value(), message, data));
    }

    public static ResponseEntity<ApiResponse> buildResponse(HttpStatus httpStatus, String message) {
        return ResponseEntity.status(httpStatus)
                .body(new ApiResponse(httpStatus.value(), message, null));
    } 
}
