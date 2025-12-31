package com.windfall.api.payment.dto.response;

import lombok.Getter;

@Getter
public class TossPaymentConfirmResponse {

  private String paymentKey;
  private String orderId;
  private Long totalAmount;
  private String method;
  private String status;
}