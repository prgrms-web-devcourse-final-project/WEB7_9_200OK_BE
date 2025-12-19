package com.windfall.global.response;

import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import org.springframework.http.HttpStatus;

public record ApiResponse<T>(
        int code,
        HttpStatus status,
        String message,
        T data
) {
    public static <T> ApiResponse<T> ok( String message, T data) {
        return new ApiResponse<>(HttpStatus.OK.value(),HttpStatus.OK, message, data);
    }

    public static <T> ApiResponse<T> ok(T data) {
        return ok("정보를 불러왔습니다.", data);
    }

    public static <T> ApiResponse<T> created( String message, T data) {
        return new ApiResponse<>(HttpStatus.CREATED.value(),HttpStatus.CREATED, message, data);
    }

    public static <T> ApiResponse<T> noContent() {
        return new ApiResponse<>(HttpStatus.NO_CONTENT.value(),HttpStatus.NO_CONTENT, null, null);
    }

    public static ApiResponse<?> fail(ErrorException errorException) {
        ErrorCode errorCode = errorException.getErrorCode();
        return new ApiResponse<>(
                errorCode.getHttpStatus().value(),
                errorCode.getHttpStatus(),
                errorCode.getMessage(),
                null
        );
    }

    public static ApiResponse<?> fail(HttpStatus status, String message) {
        return new ApiResponse<>(
                status.value(),
                status,
                message,
                null
        );
    }
}
