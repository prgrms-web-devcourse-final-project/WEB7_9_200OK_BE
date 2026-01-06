package com.windfall.api.payment.service;

import com.windfall.api.auction.service.AuctionStateService;
import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.request.TossPaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.api.payment.dto.response.TossPaymentConfirmResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.payment.repository.PaymentRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.trade.repository.TradeRepository;
import com.windfall.domain.user.repository.UserRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {
  private final WebClient webClient;
  private final PaymentRepository paymentRepository;
  private final AuctionRepository auctionRepository;
  private final TradeRepository tradeRepository;
  private final ChatRoomRepository chatRoomRepository;
  private final UserRepository userRepository;
  private final AuctionStateService auctionStateService;
  private final PaymentPostProcessService paymentPostProcessService;

  @Value("${spring.toss.secretkey}")
  private String widgetSecretKey;

  public PaymentConfirmResponse confirmPayment(
      PaymentConfirmRequest paymentConfirmRequest,
      Long buyerId) {
    log.info("Now on PaymentService");

    // DTO에서 데이터 추출하며 예외처리.
    String paymentKey = paymentConfirmRequest.paymentKey();
    String orderId = paymentConfirmRequest.orderId();
    Long amount = paymentConfirmRequest.amount();
    Long auctionId = paymentConfirmRequest.auctionId();
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));

    log.info(
        "[PaymentConfirm] request received - paymentKey={}, orderId={}, amount={}, auctionId={}",
        paymentKey,
        orderId,
        amount,
        auctionId
    );
    // 테스트용 임시 User seller 값.
    /*
    User seller = new User(
        ProviderType.KAKAO, "providerUserId", "email", "nickname", "imageUrl");
    userRepository.save(seller);
    */

    // 테스트용 임시 Auction auction 값.
    /*
    Auction auction = Auction.builder()
        .seller(seller)
        .title("auctionTitle")
        .description("auctionDescription")
        .category(AuctionCategory.CLOTHING)
        .startPrice(8888L)
        .currentPrice(7777L)
        .stopLoss(6666L)
        .dropAmount(1111L)
        .startedAt(LocalDateTime.of(2026, 1, 1, 0, 0))
        .endedAt(LocalDateTime.of(2026, 12, 31, 23, 59))
        .build();
    auctionRepository.save(auction);
    */

    Long sellerId = auction.getSeller().getId();
    if(!userRepository.existsById(sellerId)) throw new ErrorException(ErrorCode.NOT_FOUND_SELLER);
    if(!userRepository.existsById(buyerId)) throw new ErrorException(ErrorCode.NOT_FOUND_BUYER);

    log.info(
        "[PaymentConfirm] seller/buyer check start - auctionId={}, sellerId={}, buyerId={}",
        auction.getId(),
        sellerId,
        buyerId
    );

    // 더티체킹 이슈로 상태 분리
    Trade trade = tradeRepository.findByAuction(auction)
        .orElse(null);
    if (trade == null) {
      trade = Trade.builder()
          .auction(auction)
          .sellerId(sellerId)
          .buyerId(buyerId)
          .finalPrice(amount)
          .build();
      tradeRepository.save(trade); // 신규 엔티티만 save
    } else {
      trade.changeBuyer(buyerId);
    }

    final Trade tradeFixed = trade;

    // 경우 1. buyer가 본인: 본인이 1빠따. 상태 체크 필요 X
    // 경우 2. buyer가 남: trade 상태가 CANCELED나 FAILED일 때만 가능.
    if (!trade.getBuyerId().equals(buyerId)
        && (trade.getStatus() == TradeStatus.PAYMENT_CANCELED
        || trade.getStatus() == TradeStatus.PAYMENT_FAILED)) {
      throw new ErrorException(ErrorCode.PAYMENT_REQUEST_LATE);
    }


    TossPaymentConfirmRequest tossRequest = new TossPaymentConfirmRequest(paymentKey, orderId,
        amount);

    log.info(
        "[TossConfirm] request start - orderId={}, paymentKey={}, amount={}",
        tossRequest.orderId(),
        tossRequest.paymentKey(),
        tossRequest.amount()
    );

    Base64.Encoder encoder = Base64.getEncoder();
    byte[] encodedBytes = encoder.encode((widgetSecretKey + ":").getBytes(StandardCharsets.UTF_8));
    String authorization = "Basic " + new String(encodedBytes);

    TossPaymentConfirmResponse tossResponse = webClient.post()
        .uri("https://api.tosspayments.com/v1/payments/confirm")
        .header(HttpHeaders.AUTHORIZATION, authorization)
        .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
        .bodyValue(tossRequest)
        .retrieve()
        .onStatus(HttpStatusCode::isError, response -> {
          log.warn(
              "[TossConfirm] error response - httpStatus={}, orderId={}",
              response.statusCode(),
              tossRequest.orderId()
          );
          tradeFixed.changeStatus(TradeStatus.PAYMENT_FAILED);
          return Mono.error(new ErrorException(ErrorCode.PAYMENT_CONFIRM_FAILED));
        })
        .bodyToMono(TossPaymentConfirmResponse.class)
        .block();

    log.info(
        "[TossConfirm] success response received - orderId={}, status={}, paymentKey={}, totalAmount={}, method={}",
        tossResponse.orderId(),
        tossResponse.status(),
        tossResponse.paymentKey(),
        tossResponse.totalAmount(),
        tossResponse.method()
    );

    /* // 통신 제외한 로직 테스트용 임시 저장 객체
    TossPaymentConfirmResponse tossResponse = new TossPaymentConfirmResponse(
        paymentKey, orderId, 9999L, "카드 결제", "DONE");
    */

    log.info(
        "[PaymentConfirm] toss response validation start - orderId={}, totalAmount={}",
        tossResponse.orderId(),
        tossResponse.totalAmount()
    );

    if (!tossResponse.orderId().equals(paymentConfirmRequest.orderId())) {
      trade.changeStatus(TradeStatus.PAYMENT_FAILED);
      throw new ErrorException(ErrorCode.PAYMENT_ORDER_MISMATCH);
    }

    if (!tossResponse.totalAmount().equals(paymentConfirmRequest.amount())) {
      trade.changeStatus(TradeStatus.PAYMENT_FAILED);
      throw new ErrorException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }

    // db에 저장하는 로직 따로 뺌.
    paymentPostProcessService.updateDatabaseAfterPayment(auctionId,trade,paymentKey,amount);

    return new PaymentConfirmResponse(
        tossResponse.orderId(), tossResponse.paymentKey(), tossResponse.totalAmount());
  }
}
