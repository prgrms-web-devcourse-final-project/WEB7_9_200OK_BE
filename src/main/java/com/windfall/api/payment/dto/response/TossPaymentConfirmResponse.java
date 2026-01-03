package com.windfall.api.payment.dto.response;

public record TossPaymentConfirmResponse(
    String paymentKey,
    String orderId,
    Long totalAmount,
    String method,
    String status
) { }