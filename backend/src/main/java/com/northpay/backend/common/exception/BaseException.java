package com.northpay.backend.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;
import java.util.Map;

@Getter
public class BaseException extends RuntimeException {
    private final ErrorCode errorCode;
    private final HttpStatus status;
    private final Map<String, Object> details;

    public BaseException(ErrorCode errorCode, HttpStatus status, String message) {
        this(errorCode, status, message, null);
    }

    public BaseException(ErrorCode errorCode, HttpStatus status, String message, Map<String, Object> details) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
        this.details = details;
    }

    public Map<String, Object> getDetails() {
        return details;
    }
}