package com.windfall.global.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
public enum ErrorCode {


  // 유저
  NOT_FOUND_USER(HttpStatus.NOT_FOUND, "사용자를 찾을 수 없습니다."),
  NOT_FOUND_PROVIDER(HttpStatus.NOT_FOUND, "해당 provider를 찾을 수 없습니다."),
  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "올바른 토큰 값이 아닙니다."),
  EMPTY_REFRESH_TOKEN(HttpStatus.NOT_FOUND, "쿠키에 리프레시 토큰이 없습니다."),

  // 경매
  INVALID_TIME(HttpStatus.BAD_REQUEST, "경매 시간을 다시 설정해주세요."),
  INVALID_DROP_AMOUNT(HttpStatus.BAD_REQUEST, "경매 하락 가격을 다시 설정해주세요."),
  INVALID_STOP_LOSS(HttpStatus.BAD_REQUEST, "경매 Stop Loss을 다시 설정해주세요."),
  INVALID_PRICE(HttpStatus.BAD_REQUEST,"최소 가격은 최대 가격보다 클 수 없습니다."),
  NOT_FOUND_AUCTION(HttpStatus.NOT_FOUND, "존재하지 않는 경매입니다."),
  AUCTION_NOT_PROCESS(HttpStatus.BAD_REQUEST, "경매가 진행 중인 상태가 아닙니다."),
  INVALID_AUCTION_SELLER(HttpStatus.FORBIDDEN, "해당 경매의 판매자가 아닙니다."),
  AUCTION_CANNOT_DELETE(HttpStatus.CONFLICT, "현재 상태의 경매는 삭제할 수 없습니다."),
  AUCTION_CANNOT_CANCEL(HttpStatus.CONFLICT, "현재 상태의 경매는 취소할 수 없습니다."),

  // 경매 이미지
  INVALID_S3_UPLOAD(HttpStatus.BAD_GATEWAY,"S3 이미지 업로드 실패했습니다."),
  INVALID_IMAGE_STATUS(HttpStatus.BAD_REQUEST, "경매에 연결할 수 없는 이미지 상태입니다."),


  //알림
  NOT_FOUND_NOTIFICATION(HttpStatus.NOT_FOUND,"존재하지 않는 알림입니다."),
  INVALID_NOTIFICATION(HttpStatus.FORBIDDEN, "해당 유저의 알림이 아닙니다."),

  // 채팅
  NOT_FOUND_CHAT_ROOM(HttpStatus.NOT_FOUND, "존재하지 않는 채팅방입니다."),
  FORBIDDEN_CHAT_ROOM(HttpStatus.FORBIDDEN, "채팅방에 접근할 수 있는 권한이 없습니다."),
  INVALID_TRADE_STATUS_FOR_CHAT(HttpStatus.BAD_REQUEST, "현재 거래 상태에서는 채팅을 조회할 수 없습니다."),

  // 결제
  INVALID_PAYMENT_PROVIDER(HttpStatus.BAD_REQUEST, "결제 제공사를 선택하지 않았습니다."),
  INVALID_PAYMENT_METHOD(HttpStatus.BAD_REQUEST, "결제 수단을 선택하지 않았습니다."),
  INVALID_PAYMENT_TOSS_URL(HttpStatus.INTERNAL_SERVER_ERROR, "서버 속 토스 결제 승인 URL 설정에 오류가 있습니다."),
  PAYMENT_CONFIRM_FAILED(HttpStatus.BAD_REQUEST, "토스에서 해당 결제를 찾을 수 없습니다."),
  PAYMENT_ORDER_MISMATCH(HttpStatus.BAD_REQUEST, "주문번호가 일치하지 않는 보안성 에러입니다."),
  PAYMENT_AMOUNT_MISMATCH(HttpStatus.CONFLICT, "주문 가격이 일치하지 않는 보안성 에러입니다."),
  // 그 외
  UNKNOWN_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "알 수 없는 에러")
  ;

  private final HttpStatus httpStatus;
  private final String message;
}
