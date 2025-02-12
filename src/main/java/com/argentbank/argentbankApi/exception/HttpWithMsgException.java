package com.argentbank.argentbankApi.exception;

import org.springframework.http.HttpStatus;

public class HttpWithMsgException extends RuntimeException {
    private final HttpStatus status;

    public HttpWithMsgException(HttpStatus status, String message) {
        super(message);
        this.status = status;
    }

    public HttpStatus geStatus() {
        return status;
    }
}
