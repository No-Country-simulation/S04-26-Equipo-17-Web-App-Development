package com.northpay.backend.common.exception;

import org.springframework.http.HttpStatus;

public class InvalidStateTransitionException extends BaseException {
    public InvalidStateTransitionException(String message) {
        super(ErrorCode.INVALID_STATE_TRANSITION, HttpStatus.CONFLICT, message);
    }
}
