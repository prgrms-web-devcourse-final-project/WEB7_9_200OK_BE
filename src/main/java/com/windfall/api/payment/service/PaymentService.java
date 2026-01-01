package com.windfall.api.payment.service;

import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.request.TossPaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.api.payment.dto.response.TossPaymentConfirmResponse;
import com.windfall.domain.auction.repository.AuctionRepository;
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
  private final AuctionRepository auctionRepository;

  public PaymentConfirmResponse confirmPayment(
      PaymentConfirmRequest paymentConfirmRequest) {
    // 1. paymentRequest 속 변수
    // 2. 토스 api 연동용 DTO에 값 넣기
    // 3. 개발자 센터에 내 시크릿 키 사용
    // 4. 결제 승인 api 호출
    // 5. null 방어 (타임아웃, 네트워크 오류)와 서버단에서 검증
    // 6. 결제 성공/실패 로직

    String paymentKey = paymentConfirmRequest.paymentKey();
    String orderId = paymentConfirmRequest.orderId().toString();
    Long amount = paymentConfirmRequest.amount();
    Long auctionId = paymentConfirmRequest.auctionId();

    // 사전 예외처리용
    // auctionRepository.findById(auctionId).orElseThrow(() -> new ErrorException(HttpStatus.NOT_FOUND_AUCTION));

    // 사전 처리용
    // 해당 auctionId 가진 trade 객체 있는가? -> 있다면 누군가 먼저 결제 진행 중임 -> 예외처리
    // trade 객체가 있다면 상태가 토스 결제를 진행할 수 있는 상태인가? -> 진행할 수 없는 상태라면 예외처리
    // trade 객체가 없다면 trade 객체만 바로 생성하고 저장.

    TossPaymentConfirmRequest tossRequest = new TossPaymentConfirmRequest(paymentKey, orderId, amount);

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

    if (!tossResponse.getOrderId().equals(paymentConfirmRequest.orderId().toString())) {
      throw new ErrorException(ErrorCode.PAYMENT_ORDER_MISMATCH);
    }

    if (!tossResponse.getTotalAmount().equals(paymentConfirmRequest.amount())) {
      throw new ErrorException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }

    // 사후 처리용
    // trade 객체, payment 객체 값과 status 변경, 저장.
    // payment 객체 생성, 값 넣고 저장.
    // 채팅방 객체도 생성, 저장.

    return new PaymentConfirmResponse(
        tossResponse.getOrderId(), tossResponse.getPaymentKey(), tossResponse.getTotalAmount());
  }
}
