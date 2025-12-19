package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.response.message.SellerEmojiMessage;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.enums.EmojiType;
import com.windfall.domain.auction.repository.AuctionRepository;
import com.windfall.global.exception.ErrorCode;
import com.windfall.global.exception.ErrorException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuctionInteractionService {

  private final AuctionRepository auctionRepository;
  private final SimpMessagingTemplate messagingTemplate;

  @Transactional(readOnly = true)
  public void broadcastSellerEmoji(Long auctionId, Long userId, EmojiType emojiType) {
    Auction auction = findAuctionById(auctionId);

    if(!auction.isSeller(userId)) {
      log.warn("이모지 방송 실패 - 판매자 아님 ( 경매 ID: {}, 사용자 ID: {} )", auctionId, userId);
      return;
    }

    SellerEmojiMessage message = SellerEmojiMessage.of(auctionId, emojiType);
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);

    log.info("이모지 방송 성공 ( 경매 ID: {}, 이모지: {} )", auctionId, emojiType);
  }

  private Auction findAuctionById(Long auctionId) {
    return auctionRepository.findById(auctionId)
        .orElseThrow(() -> new ErrorException(ErrorCode.NOT_FOUND_AUCTION));
  }
}
