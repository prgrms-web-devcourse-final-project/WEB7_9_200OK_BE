package com.windfall.api.auction.service;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionPriceHistory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class AuctionSchedulerService {

  private final AuctionPriceHistoryRepository historyRepository;
  private final AuctionRepository auctionRepository;
  private final RedisTemplate<String, String> redisTemplate;
  private final SimpMessagingTemplate messagingTemplate;

  public void openScheduledAuctions(LocalDateTime now) {
    List<Auction> startingAuctions = auctionRepository.findAllByStatusAndStartedAtLessThanEqual(
        AuctionStatus.SCHEDULED, now
    );

    for (Auction auction : startingAuctions) {
      auction.updateStatus(AuctionStatus.PROCESS);
      log.info("✅경매 시작 처리 완료 ( 경매 ID: {}, 제목: {} )", auction.getId(), auction.getTitle());
    }
  }

  public void dropAuctionPrices(LocalDateTime now) {
    List<Auction> activeAuctions = auctionRepository.findAllByStatus(AuctionStatus.PROCESS);

    for(Auction auction : activeAuctions) {
      long minutesElapsed = java.time.Duration.between(auction.getStartedAt(), now).toMinutes();
      long dropCount = minutesElapsed / 5;

      if(dropCount > 0) {
        long totalDiscount = dropCount * auction.getDropAmount();
        long targetPrice = auction.getStartPrice() - totalDiscount;

        if(targetPrice < auction.getStopLoss()) {
          log.info("❌경매 유찰 ( 경매 ID: {}, StopLoss 도달)", auction.getId());

          auction.updateCurrentPrice(auction.getStopLoss());
          savePriceHistoryWithViewers(auction, auction.getStopLoss());

          auction.updateStatus(AuctionStatus.FAILED);

          sendAuctionUpdate(auction.getId(), auction.getStopLoss(), AuctionStatus.FAILED);
        }
        else {
          if(targetPrice < auction.getCurrentPrice()) {
            long oldPrice = auction.getCurrentPrice();

            auction.updateCurrentPrice(targetPrice);
            savePriceHistoryWithViewers(auction, targetPrice);

            sendAuctionUpdate(auction.getId(), targetPrice, AuctionStatus.PROCESS);

            log.info("📉경매 가격 하락 처리 완료 ( 경매 ID: {}, 가격: {} -> {}",
                auction.getId(), oldPrice, targetPrice);
          }
        }
      }
    }
  }

  private void savePriceHistoryWithViewers(Auction auction, long targetPrice) {
    String redisKey = "auction:" + auction.getId() + ":viewers";

    Long viewerCount = redisTemplate.opsForSet().size(redisKey);
    if (viewerCount == null) {
      viewerCount = 0L;
    }

    AuctionPriceHistory history = AuctionPriceHistory.create(auction, targetPrice, viewerCount);
    historyRepository.save(history);
  }

  private void sendAuctionUpdate(long auctionId, long currentPrice, AuctionStatus status) {
    Map<String, Object> message = new HashMap<>();
    message.put("auctionId", auctionId);
    message.put("currentPrice", currentPrice);
    message.put("status", status);

    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);
  }
}
