package com.windfall.api.payment.dto.response;

import lombok.Getter;

@Getter
public class PaymentConfirmResponse {

  private Long paymentId;
  private String orderId;
  private String paymentKey;
  private Long price;
  private String method;
  private String status;

  public PaymentConfirmResponse(
      long paymentId, String orderId, String paymentKey,
      long price, String method, String status) {
    this.paymentId = paymentId;
    this.orderId = orderId;
    this.paymentKey = paymentKey;
    this.price = price;
    this.method = method;
    this.status = status;
  }
}
