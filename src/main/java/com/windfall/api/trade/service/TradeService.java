package com.windfall.api.trade.service;

import com.windfall.domain.trade.entity.Trade;
import com.windfall.domain.trade.enums.TradeStatus;
import com.windfall.domain.trade.repository.TradeRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class TradeService {

  private final TradeRepository tradeRepository;

  @Transactional
  public void purchaseConfirmTrade(Long userId, Long tradeId){

    Trade trade = tradeRepository.findById(tradeId).orElseThrow(
        () -> new ErrorException(ErrorCode.NOT_FOUND_TRADE));

    //payment가 있는지
    isPaymentCompleted(trade.getStatus());

    //tradeid에 해당하는 buyerid가 유효한지 검증
    validateUser(trade.getBuyerId(), userId);

    //변경
    trade.changeStatus(TradeStatus.PURCHASE_CONFIRMED);
  }

  private void isPaymentCompleted(TradeStatus status){
    if(!status.equals(TradeStatus.PAYMENT_COMPLETED)){
      throw new ErrorException(ErrorCode.NOT_PAYMENT_COMPLETED_TRADE);
    }
  }

  private void validateUser(Long buyerId, Long userId){
    if(!buyerId.equals(userId)){
      throw new ErrorException(ErrorCode.NOT_MATCHED_BUYER_ID);
    }
  }

}
