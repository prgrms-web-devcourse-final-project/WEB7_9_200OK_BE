package com.windfall.domain.payment.enums;

import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
  BANK_TRANSFER("TRANSFER", "계좌이체"),
  CREDIT_CARD("CARD", "신용·체크카드"),
  MOBILE_PAYMENT("MOBILE_PHONE", "핸드폰 결제"),
  TOSS_PAY("TOSS_PAY", "토스페이"),
  PAYCO("PAYCO", "페이코"),
  KAKAO_PAY("KAKAO_PAY", "카카오페이"),
  NAVER_PAY("NAVER_PAY", "네이버페이");

  private final String tossCode;
  private final String description;

  public static PaymentMethod fromToss(String tossMethod) {
    for (PaymentMethod method : values()) {
      if (method.tossCode.equalsIgnoreCase(tossMethod)) {
        return method;
      }
    }
    throw new ErrorException(ErrorCode.INVALID_PAYMENT_METHOD);
  }
}
