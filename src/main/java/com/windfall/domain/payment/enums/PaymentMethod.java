package com.windfall.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentMethod {
  BANK_TRANSFER("계좌이체"),
  CREDIT_CARD("신용-체크카드"),
  TOSS_PAY("토스페이"),
  PAYCO("페이코"),
  KAKAO_PAY("카카오페이"),
  NAVER_PAY("네이버페이"),
  MOBILE_PAYMENT("핸드폰 결제");

  private final String description;
}
