package com.argentbank.argentbankApi.Utils;

import com.argentbank.argentbankApi.model.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

public class ResponseUtil {
    public static ResponseEntity<ApiResponse> buildResponse(HttpStatus httpStatus, String message, Object data) {
        return ResponseEntity.status(httpStatus)
                .body(new ApiResponse(httpStatus.value(), message, data));
    }
}
