package com.windfall.api.payment.dto.response;

import lombok.Getter;

@Getter
public class PaymentConfirmResponse {

  private String orderId;
  private String paymentKey;
  private Long price;
  private String method;
  private String status;

  public PaymentConfirmResponse(
      long paymentId, String orderId, String paymentKey,
      long price, String method, String status) {
    this.orderId = orderId;
    this.paymentKey = paymentKey;
    this.price = price;
    this.method = method;
    this.status = status;
  }
}
// status, amount, orderId 만 넣자.
// orderId를 UUID로 보내주신 만큼, 반환도 UUID로 반환하자