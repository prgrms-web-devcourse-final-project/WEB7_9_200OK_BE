package com.windfall.api.payment.dto.response;

import lombok.Getter;

@Getter
public class PaymentConfirmResponse {

  private String orderId;
  private String paymentKey;
  private Long amount;

  public PaymentConfirmResponse(String orderId, String paymentKey, long amount) {
    this.orderId = orderId;
    this.paymentKey = paymentKey;
    this.amount = amount;
  }
}
// status, amount, orderId 만 넣자.
// orderId를 UUID로 보내주신 만큼, 반환도 UUID로 반환하자