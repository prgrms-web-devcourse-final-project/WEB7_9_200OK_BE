package com.windfall.api.payment.service;

import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.request.TossPaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.api.payment.dto.response.TossPaymentConfirmResponse;
import com.windfall.domain.payment.enums.PaymentProvider;
import com.windfall.domain.payment.repository.PaymentRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class PaymentService {

  private final WebClient webClient;
  private final PaymentRepository paymentRepository;

  public PaymentConfirmResponse confirmPayment(
      PaymentConfirmRequest paymentConfirmRequest) {
    // 0. trade 객체 생성
    // 1. paymentRequest 속 변수 (ㅇㅋ)
    // 2. 토스 api 연동용 DTO에 값 넣기 (ㅇㅋ)
    // 3. 개발자 센터에 내 시크릿 키 사용 (ㅇㅋ)
    // 4. 결제 승인 api 호출 (ㅇㅋ)
    // 5. null 방어 (타임아웃, 네트워크 오류)와 서버단에서 검증 (ㅇㅋ)
    // 5. 결제 성공/실패 로직

    String paymentKey = paymentConfirmRequest.paymentKey();
    String orderId = paymentConfirmRequest.orderId();
    Long price = paymentConfirmRequest.price();
    PaymentProvider paymentProvider = PaymentProvider.valueOf(
        paymentConfirmRequest.paymentProvider().toUpperCase());

    // 현재는 토스 결제 api가 아니면 에러 반환
    if (paymentProvider != PaymentProvider.TOSS) {
      throw new ErrorException(ErrorCode.INVALID_PAYMENT_PROVIDER);
    }

    TossPaymentConfirmRequest tossRequest = new TossPaymentConfirmRequest(paymentKey, orderId,
        price);

    String widgetSecretKey = "test_sk_eqRGgYO1r5M99yPAxBgnrQnN2Eya";
    Base64.Encoder encoder = Base64.getEncoder();
    byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    String authorization = "Basic " + new String(encodedBytes);

    TossPaymentConfirmResponse tossResponse = webClient.post()
        .uri("https://api.tosspayments.com/v1/payments/confirm")
        .header(HttpHeaders.AUTHORIZATION, authorization)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(tossRequest)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response ->
            Mono.error(new ErrorException(ErrorCode.PAYMENT_CONFIRM_FAILED)))
        .bodyToMono(TossPaymentConfirmResponse.class)
        .block();

    if (tossResponse == null) {
      throw new ErrorException(ErrorCode.PAYMENT_CONFIRM_FAILED);
    }

    if (!tossResponse.getOrderId().equals(paymentConfirmRequest.orderId())) {
      throw new ErrorException(ErrorCode.PAYMENT_ORDER_MISMATCH);
    }

    if (!tossResponse.getTotalAmount().equals(paymentConfirmRequest.price())) {
      throw new ErrorException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }

    PaymentConfirmResponse paymentConfirmResponse = new PaymentConfirmResponse(
        1L, tossResponse.getOrderId(), tossResponse.getPaymentKey(),
        tossResponse.getTotalAmount(), tossResponse.getMethod(), "DONE");

    return paymentConfirmResponse;

    /*
    return new PaymentConfirmResponse(
        1L, "orderId", "paymentKey", 999L, "MOBILE_PAYMENT", "DONE");
    */
  }

}
