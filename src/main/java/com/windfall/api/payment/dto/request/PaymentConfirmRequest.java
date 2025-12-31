package com.windfall.api.payment.dto.request;

public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long price,
    String paymentProvider,
    String paymentMethod
){}