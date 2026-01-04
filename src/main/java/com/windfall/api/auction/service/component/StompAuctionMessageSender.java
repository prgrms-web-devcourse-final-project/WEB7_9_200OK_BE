package com.windfall.api.auction.service.component;

import com.windfall.api.auction.dto.response.message.AuctionMessage;
import com.windfall.api.auction.dto.response.message.AuctionViewerMessage;
import com.windfall.api.auction.dto.response.message.SellerEmojiMessage;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.enums.EmojiType;
import lombok.RequiredArgsConstructor;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Async("socketTaskExecutor")
public class StompAuctionMessageSender implements AuctionMessageSender {

  private final SimpMessagingTemplate messagingTemplate;

  @Override
  public void broadcastPriceUpdate(Long auctionId, Long currentPrice, AuctionStatus status) {
    AuctionMessage message = AuctionMessage.of(auctionId, currentPrice, status);
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);
  }

  @Override
  public void broadcastViewerCount(Long auctionId, long viewerCount) {
    AuctionViewerMessage message = AuctionViewerMessage.of(auctionId, viewerCount);
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);

  }

  @Override
  public void broadcastSellerEmoji(Long auctionId, EmojiType emojiType) {
    SellerEmojiMessage message = SellerEmojiMessage.of(auctionId, emojiType);
    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);
  }
}
