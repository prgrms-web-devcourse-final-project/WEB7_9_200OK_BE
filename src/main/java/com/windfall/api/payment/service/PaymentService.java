package com.windfall.api.payment.service;

import com.windfall.api.auction.service.AuctionStateService;
import com.windfall.api.payment.dto.request.PaymentConfirmRequest;
import com.windfall.api.payment.dto.request.TossPaymentConfirmRequest;
import com.windfall.api.payment.dto.response.PaymentConfirmResponse;
import com.windfall.api.payment.dto.response.TossPaymentConfirmResponse;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.payment.entity.Payment;
import com.windfall.domain.payment.entity.PaymentSelection;
import com.windfall.domain.payment.enums.PaymentMethod;
import com.windfall.domain.payment.enums.PaymentProvider;
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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

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

  @Value("${spring.toss.secretkey}")
  private String widgetSecretKey;

  @Transactional
  public PaymentConfirmResponse confirmPayment(
      PaymentConfirmRequest paymentConfirmRequest,
      Long buyerId) {

    // DTO에서 데이터 추출하며 예외처리.
    String paymentKey = paymentConfirmRequest.paymentKey();
    String orderId = paymentConfirmRequest.orderId();
    Long amount = paymentConfirmRequest.amount();
    Long auctionId = paymentConfirmRequest.auctionId();
    Auction auction = auctionRepository.findById(auctionId).orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));

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
          tradeFixed.changeStatus(TradeStatus.PAYMENT_FAILED);
          return Mono.error(new ErrorException(ErrorCode.PAYMENT_CONFIRM_FAILED));
        })
        .bodyToMono(TossPaymentConfirmResponse.class)
        .block();

    /* // 통신 제외한 로직 테스트용 임시 저장 객체
    TossPaymentConfirmResponse tossResponse = new TossPaymentConfirmResponse(
        paymentKey, orderId, 9999L, "카드 결제", "DONE");
    */

    if (!tossResponse.orderId().equals(paymentConfirmRequest.orderId())) {
      trade.changeStatus(TradeStatus.PAYMENT_FAILED);
      throw new ErrorException(ErrorCode.PAYMENT_ORDER_MISMATCH);
    }

    if (!tossResponse.totalAmount().equals(paymentConfirmRequest.amount())) {
      trade.changeStatus(TradeStatus.PAYMENT_FAILED);
      throw new ErrorException(ErrorCode.PAYMENT_AMOUNT_MISMATCH);
    }

    // 사후 처리용
    // trade 객체, payment 객체 값과 status 변경, 저장.
    // payment 객체 생성, 값 넣고 저장.
    // 채팅방 객체도 생성, 저장.

    auctionStateService.completeAuction(auctionId);
    trade.changeStatus(TradeStatus.PAYMENT_COMPLETED);

    Payment payment = Payment.confirm(trade.getId(), paymentKey, amount,
        new PaymentSelection(PaymentProvider.TOSS, PaymentMethod.MOBILE_PAYMENT));
    paymentRepository.save(payment);

    ChatRoom chatRoom = ChatRoom.generateChatRoom(trade);
    chatRoomRepository.save(chatRoom);

    return new PaymentConfirmResponse(
        tossResponse.orderId(), tossResponse.paymentKey(), tossResponse.totalAmount());
  }
}
