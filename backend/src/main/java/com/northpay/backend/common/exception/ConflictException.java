package com.northpay.backend.common.exception;

import org.springframework.http.HttpStatus;
import java.util.Map;

public class ConflictException extends BaseException {
    public ConflictException(String message) {
        super(ErrorCode.CONFLICT, HttpStatus.CONFLICT, message);
    }

    public ConflictException(String message, Map<String, Object> details) {
        super(ErrorCode.CONFLICT, HttpStatus.CONFLICT, message, details);
    }
}