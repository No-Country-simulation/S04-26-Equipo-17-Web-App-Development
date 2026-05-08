package com.northpay.backend.common.exception;

public enum ErrorCode {
    RESOURCE_NOT_FOUND,
    INVALID_TOKEN,
    TOKEN_EXPIRED,
    INVALID_STATE_TRANSITION,
    VALIDATION_ERROR,
    EMAIL_SEND_FAILURE,
    INTERNAL_ERROR
}