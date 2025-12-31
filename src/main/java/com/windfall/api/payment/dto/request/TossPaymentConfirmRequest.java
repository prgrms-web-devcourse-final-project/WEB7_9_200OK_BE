package com.windfall.api.payment.dto.request;

public record TossPaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount
) {}
