package com.windfall.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {

  // 유저
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),

  // 경매
  NOT_FOUND_AUCTION(HttpStatus.NOT_FOUND, "존재하지 않는 경매입니다."),
  AUCTION_NOT_PROCESS(HttpStatus.BAD_REQUEST, "경매가 진행 중인 상태가 아닙니다."),
  INVALID_AUCTION_SELLER(HttpStatus.FORBIDDEN, "해당 경매의 판매자가 아닙니다."),

  // 그 외
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러")
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
