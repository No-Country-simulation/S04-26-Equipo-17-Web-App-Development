package com.northpay.backend.common.exception;

import org.springframework.http.HttpStatus;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, HttpStatus.CONFLICT, message);
    }
}
