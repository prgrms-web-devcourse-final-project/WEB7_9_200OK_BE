package com.windfall.global.exception;

import com.windfall.global.response.ApiResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.HandlerMethodValidationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
    // 커스텀 예외 처리
    @ExceptionHandler(ErrorException.class)
    protected ApiResponse<?> handleCustomException(ErrorException e) {
        log.error("ErrorException: {} - {}", e.getErrorCode().name(), e.getMessage(), e);
        return ApiResponse.fail(e);
    }

  // @Valid 유효성 검사 실패 시 발생하는 예외 처리
  @ExceptionHandler({
      MethodArgumentNotValidException.class,
      HandlerMethodValidationException.class
  })
  public ApiResponse<?> handleMethodArgumentNotValid(Exception e) {
    String message = "";
    if(e instanceof MethodArgumentNotValidException methodException){
      message = methodException.getBindingResult().getFieldError().getDefaultMessage();
    }

    if(e instanceof HandlerMethodValidationException handlerException){
      message = handlerException.getParameterValidationResults()
          .getFirst()
          .getResolvableErrors()
          .getFirst()
          .getDefaultMessage();
    }

    log.warn("MethodArgumentNotValidException {}", e.getMessage());
    return ApiResponse.fail(HttpStatus.BAD_REQUEST,message);
  }

    // 그 외 모든 예외 처리
    @ExceptionHandler(Exception.class)
    public ApiResponse<?> handleAllException(final Exception e) {
        log.error("handleAllException {}", e.getMessage(), e);
        return ApiResponse.fail(new ErrorException(ErrorCode.UNKNOWN_ERROR));
    }
}
