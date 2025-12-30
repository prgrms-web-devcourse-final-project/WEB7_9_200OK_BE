package com.windfall.domain.payment.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum PaymentStatus {
  REQUESTED("주문 생성 단계"),
  READY("가상계좌 발급 등"),
  IN_PROGRESS("결제 진행 중"),
  DONE("결제 승인 완료"),
  CANCELED("결제 취소/환불 완료"),
  FAILED("결제 실패");
  
  private final String description;
}
