package com.windfall.global.exception;

import lombok.Getter;

@Getter
public class ErrorException extends RuntimeException{
    private final ErrorCode errorCode;

    public ErrorException(ErrorCode errorCode) {
        super(errorCode.getMessage());
        this.errorCode = errorCode;
    }

    public ErrorException(String message, ErrorCode errorCode) {
        super(message);
        this.errorCode = errorCode;
    }
}
