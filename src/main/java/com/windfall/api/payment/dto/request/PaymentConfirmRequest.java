package com.windfall.api.payment.dto.request;

public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long price,
    String paymentProvider,
    String paymentMethod
){}

/*
*
* // 이거로 변경하자.
public record PaymentConfirmRequest(
    String paymentKey,
    String orderId,
    Long amount,
    String auctionId
){}
* orderId는 사용자별 고유한 값으로 설정해놨습니당 uuid 만들어서 보내도록 설정해놨어요!
* */