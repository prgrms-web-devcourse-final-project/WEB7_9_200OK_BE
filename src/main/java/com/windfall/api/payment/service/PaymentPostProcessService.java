package com.windfall.api.payment.service;

import com.windfall.api.auction.service.AuctionStateService;
import com.windfall.domain.chat.entity.ChatRoom;
import com.windfall.domain.chat.repository.ChatRoomRepository;
import com.windfall.domain.payment.entity.Payment;
import com.windfall.domain.payment.entity.PaymentSelection;
import com.windfall.domain.payment.enums.PaymentMethod;
import com.windfall.domain.payment.enums.PaymentProvider;
import com.windfall.domain.payment.repository.PaymentRepository;
import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class PaymentPostProcessService {

  AuctionStateService auctionStateService;
  PaymentRepository paymentRepository;
  ChatRoomRepository chatRoomRepository;

  public PaymentPostProcessService(
      AuctionStateService auctionStateService,
      PaymentRepository paymentRepository,
      ChatRoomRepository chatRoomRepository
  ) {
    this.auctionStateService = auctionStateService;
    this.paymentRepository = paymentRepository;
    this.chatRoomRepository = chatRoomRepository;
  }

  @Transactional
  public void updateDatabaseAfterPayment(Long auctionId, Trade trade, String paymentKey, Long amount) {
    // 사후 처리용
    // trade 객체, payment 객체 값과 status 변경, 저장.
    // payment 객체 생성, 값 넣고 저장.
    // 채팅방 객체도 생성, 저장.
    log.info(
        "[PaymentConfirm] auction completion start - auctionId={}",
        auctionId
    );
    auctionStateService.completeAuction(auctionId);
    log.info(
        "[PaymentConfirm] auction completed - auctionId={}",
        auctionId
    );

    log.info(
        "[PaymentConfirm] trade status change - tradeId={}, from={}, to={}",
        trade.getId(),
        trade.getStatus(),
        TradeStatus.PAYMENT_COMPLETED
    );
    trade.changeStatus(TradeStatus.PAYMENT_COMPLETED);

    log.info(
        "[PaymentConfirm] payment entity create - tradeId={}, paymentKey={}, amount={}, provider={}, method={}",
        trade.getId(),
        paymentKey,
        amount,
        PaymentProvider.TOSS,
        PaymentMethod.MOBILE_PAYMENT
    );
    Payment payment = Payment.confirm(trade.getId(), paymentKey, amount,
        new PaymentSelection(PaymentProvider.TOSS, PaymentMethod.MOBILE_PAYMENT));
    paymentRepository.save(payment);
    log.info(
        "[PaymentConfirm] payment saved - paymentId={}, tradeId={}",
        payment.getId(),
        trade.getId()
    );

    log.info(
        "[PaymentConfirm] chat room create - tradeId={}",
        trade.getId()
    );
    ChatRoom chatRoom = ChatRoom.generateChatRoom(trade);
    chatRoomRepository.save(chatRoom);
    log.info(
        "[PaymentConfirm] chat room saved - chatRoomId={}, tradeId={}",
        chatRoom.getId(),
        trade.getId()
    );
  }
}
