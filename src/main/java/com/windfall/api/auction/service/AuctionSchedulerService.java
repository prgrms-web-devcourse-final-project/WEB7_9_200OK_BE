package com.windfall.api.auction.service;

import com.windfall.api.auction.dto.response.message.AuctionMessage;
import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionPriceHistory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
  private final SimpMessagingTemplate messagingTemplate;
  private final AuctionViewerService viewerService;

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
    long viewerCount = viewerService.getViewerCount(auction.getId());

    AuctionPriceHistory history = AuctionPriceHistory.create(auction, targetPrice, viewerCount);
    historyRepository.save(history);
  }

  private void sendAuctionUpdate(long auctionId, long currentPrice, AuctionStatus status) {

    AuctionMessage message = AuctionMessage.from(auctionId, currentPrice, status);

    messagingTemplate.convertAndSend("/topic/auction/" + auctionId, message);
  }
}
