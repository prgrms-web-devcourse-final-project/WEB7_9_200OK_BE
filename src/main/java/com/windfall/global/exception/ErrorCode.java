package com.windfall.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  NOT_FOUND_USER(HttpStatus.UNAUTHORIZED, "사용자를 찾을 수 없습니다."),
  UNKNOWN_ERROR(HttpStatus.UNAUTHORIZED, "알 수 없는 에러")
  ;
  private final HttpStatus httpStatus;
  private final String message;
}
