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
