package com.argentbank.argentbankApi.model.response;

public record ApiResponse(int status, String message, Object body) {
}
