package com.windfall.api.auction.service;

import static com.windfall.global.exception.ErrorCode.NOT_FOUND_AUCTION;
import static com.windfall.global.exception.ErrorCode.NOT_FOUND_USER;

import com.windfall.api.auction.service.component.AuctionMessageSender;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionInteractionService {

  private final AuctionRepository auctionRepository;
  private final AuctionMessageSender messageSender;

  @Transactional(readOnly = true)
  public void broadcastSellerEmoji(Long auctionId, Long userId, EmojiType emojiType) {

    validateUser(userId);
    Auction auction = findAuctionById(auctionId);

    if(!auction.isSeller(userId)) {
      log.warn("이모지 방송 실패 - 판매자 아님 ( 경매 ID: {}, 사용자 ID: {} )", auctionId, userId);
      return;
    }

    messageSender.broadcastSellerEmoji(auctionId, emojiType);

    log.info("이모지 방송 성공 ( 경매 ID: {}, 이모지: {} )", auctionId, emojiType);
  }

  private Auction findAuctionById(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(NOT_FOUND_AUCTION));
  }

  private void validateUser(Long userId) {
    if (userId == null) {
      throw new ErrorException(NOT_FOUND_USER);
    }
  }
}
