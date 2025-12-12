package com.windfall.api.auction.scheduler;

import com.windfall.domain.auction.entity.Auction;
import com.windfall.domain.auction.entity.AuctionPriceHistory;
import com.windfall.domain.auction.enums.AuctionStatus;
import com.windfall.domain.auction.repository.AuctionPriceHistoryRepository;
import com.windfall.domain.auction.repository.AuctionRepository;
import java.time.LocalDateTime;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuctionScheduler {

  private final AuctionRepository auctionRepository;
  private final AuctionPriceHistoryRepository historyRepository;

  @Scheduled(cron = "0 0/5 * * * *")
  @Transactional
  public void runAuctionScheduler() {
    LocalDateTime now = LocalDateTime.now();
    log.info("스케줄러 실행: {}", now);

    openScheduledAuctions(now);

    dropAuctionPrices(now);
  }

  private void openScheduledAuctions(LocalDateTime now) {
    List<Auction> startingAuctions = auctionRepository.findAllByStatusAndStartedAtLessThanEqual(
        AuctionStatus.SCHEDULED, now
    );

    for (Auction auction : startingAuctions) {
      auction.updateStatus(AuctionStatus.PROCESS);
      log.info("경매 시작 처리 완료 ( 경매 ID: {}, 제목: {} )", auction.getId(), auction.getTitle());
    }
  }

  private void dropAuctionPrices(LocalDateTime now) {
    List<Auction> activeAuctions = auctionRepository.findAllByStatus(AuctionStatus.PROCESS);

    for(Auction auction : activeAuctions) {
      long minutesElapsed = java.time.Duration.between(auction.getStartedAt(), now).toMinutes();
      long dropCount = minutesElapsed / 5;

      if(dropCount > 0) {
        long totalDiscount = dropCount * auction.getDropAmount();
        long targetPrice = auction.getStartPrice() - totalDiscount;

        if(targetPrice < auction.getStopLoss()) {
          log.info("경매 유찰 ( 경매 ID: {}, StopLoss 도달)", auction.getId());
          auction.updateStatus(AuctionStatus.FAILED);

          auction.updateCurrentPrice(auction.getStopLoss());
        }
        else {
          if(targetPrice < auction.getCurrentPrice()) {
            auction.updateCurrentPrice(targetPrice);
            savePriceHistory(auction, targetPrice);
            log.info("경매 가격 하락 처리 완료 ( 경매 ID: {}, 가격: {} -> {}",
                auction.getId(), auction.getCurrentPrice(), targetPrice);
          }
        }
      }
    }
  }

  private void savePriceHistory(Auction auction, long targetPrice) {
    AuctionPriceHistory history = AuctionPriceHistory.create(auction, targetPrice, 0L);
    historyRepository.save(history);
  }
}
