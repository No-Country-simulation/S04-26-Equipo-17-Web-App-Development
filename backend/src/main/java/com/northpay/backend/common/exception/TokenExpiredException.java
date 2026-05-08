package com.northpay.backend.common.exception;

import org.springframework.http.HttpStatus;

public class TokenExpiredException extends BaseException {
    public TokenExpiredException(String message) {
        super(ErrorCode.TOKEN_EXPIRED, HttpStatus.GONE, message);
    }
}