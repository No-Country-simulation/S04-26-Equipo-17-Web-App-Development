package com.northpay.backend.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidTokenException extends BaseException {
    public InvalidTokenException(String message) {
        super(ErrorCode.INVALID_TOKEN, HttpStatus.UNAUTHORIZED, message);
    }
}