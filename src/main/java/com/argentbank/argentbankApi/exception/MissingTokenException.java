package com.argentbank.argentbankApi.exception;

public class MissingTokenException extends RuntimeException {

    public MissingTokenException(String msg) {
        super(msg);
    }
}
